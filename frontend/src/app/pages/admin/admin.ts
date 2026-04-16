import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, interval } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { MatCardModule }       from '@angular/material/card';
import { MatIconModule }       from '@angular/material/icon';
import { MatButtonModule }     from '@angular/material/button';
import { MatBadgeModule }      from '@angular/material/badge';
import { MatDividerModule }    from '@angular/material/divider';
import { MatChipsModule }      from '@angular/material/chips';
import { MatTableModule }      from '@angular/material/table';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule }    from '@angular/material/tooltip';

import { DashboardService }   from '../../services/dashboard.service';
import { EmpilhadeiraService } from '../../services/empilhadeira.service';
import { AuthService }        from '../../services/auth.service';
import {
  DashboardResumo, EmpilhadeiraStatus, ChecklistRecente,
  ChecklistPorTurno, Perfil
} from '../../models/api.models';

@Component({
  selector: 'app-admin',
  standalone: true,
  templateUrl: './admin.html',
  styleUrls: ['./admin.css'],
  imports: [
    CommonModule,
    MatCardModule, MatIconModule, MatButtonModule, MatBadgeModule,
    MatDividerModule, MatChipsModule, MatTableModule,
    MatSnackBarModule, MatTooltipModule
  ]
})
export class AdminComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // Dados
  resumo: DashboardResumo | null = null;
  empilhadeiras: EmpilhadeiraStatus[] = [];
  checklistsRecentes: ChecklistRecente[] = [];
  checklistsPorTurno: ChecklistPorTurno[] = [];

  // Estado
  carregando      = true;
  ultimaAtualizacao = new Date();
  alertasVivos: string[] = [];
  eventSource?: EventSource;

  // Perfil
  nomeUsuario = '';
  perfil: Perfil | null = null;

  // Colunas das tabelas
  colunasEmpilhadeiras = ['modelo', 'tipo', 'capacidade', 'status', 'ultimoChecklist', 'acoes'];
  colunasChecklists    = ['hora', 'empilhadeira', 'operador', 'turno', 'resultado'];

  constructor(
    private dashboardService: DashboardService,
    private empilhadeiraService: EmpilhadeiraService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.nomeUsuario = user.nomeCompleto;
      this.perfil = user.perfil;
    }

    this.carregarTudo();
    this.conectarSSE();

    // Atualiza resumo a cada 30s como fallback (caso SSE caia)
    interval(30_000).pipe(takeUntil(this.destroy$))
      .subscribe(() => this.carregarResumo());
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.eventSource?.close();
  }

  carregarTudo(): void {
    this.carregando = true;
    this.carregarResumo();
    this.carregarEmpilhadeiras();
    this.carregarChecklistsRecentes();
    this.carregarPorTurno();
  }

  carregarResumo(): void {
    this.dashboardService.getResumo()
      .pipe(takeUntil(this.destroy$))
      .subscribe({ next: r => { this.resumo = r; this.ultimaAtualizacao = new Date(); this.carregando = false; } });
  }

  carregarEmpilhadeiras(): void {
    const obs = this.podeVerTodasEmpilhadeiras()
      ? this.dashboardService.getStatusEmpilhadeiras()
      : this.dashboardService.getBloqueadas();
    obs.pipe(takeUntil(this.destroy$))
       .subscribe({ next: e => this.empilhadeiras = e });
  }

  carregarChecklistsRecentes(): void {
    if (!this.podeVerChecklists()) return;
    this.dashboardService.getChecklistsRecentes(15)
      .pipe(takeUntil(this.destroy$))
      .subscribe({ next: c => this.checklistsRecentes = c });
  }

  carregarPorTurno(): void {
    if (!this.podeVerChecklists()) return;
    this.dashboardService.getChecklistsPorTurno()
      .pipe(takeUntil(this.destroy$))
      .subscribe({ next: t => this.checklistsPorTurno = t });
  }

  conectarSSE(): void {
    try {
      this.eventSource = this.dashboardService.conectarEventos();

      this.eventSource.addEventListener('empilhadeira_bloqueada', (e: MessageEvent) => {
        const dados = JSON.parse(e.data);
        this.adicionarAlerta(`🚫 Empilhadeira ${dados.modelo} bloqueada: ${dados.motivo}`);
        this.carregarResumo();
        this.carregarEmpilhadeiras();
      });

      this.eventSource.addEventListener('empilhadeira_desbloqueada', (e: MessageEvent) => {
        const dados = JSON.parse(e.data);
        this.adicionarAlerta(`✅ Empilhadeira ${dados.modelo} desbloqueada`);
        this.carregarResumo();
        this.carregarEmpilhadeiras();
      });

      this.eventSource.addEventListener('checklist_salvo', (e: MessageEvent) => {
        const dados = JSON.parse(e.data);
        const icone = dados.resultado === 'REPROVADO' ? '⚠️' : '✔️';
        this.adicionarAlerta(`${icone} Checklist ${dados.resultado} — ${dados.modelo} (${dados.operador})`);
        this.carregarResumo();
        this.carregarChecklistsRecentes();
        this.carregarPorTurno();
      });

      this.eventSource.onerror = () => {
        // Reconecta após 5s se a conexão cair
        setTimeout(() => this.conectarSSE(), 5000);
      };
    } catch {
      // SSE não disponível (ex: dev sem backend) — ignora silenciosamente
    }
  }

  adicionarAlerta(msg: string): void {
    this.alertasVivos.unshift(msg);
    if (this.alertasVivos.length > 10) this.alertasVivos.pop();
    this.snackBar.open(msg, 'Fechar', { duration: 4000 });
  }

  desbloquear(id: number, modelo: string): void {
    this.empilhadeiraService.desbloquear(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.snackBar.open(`✅ ${modelo} desbloqueada`, 'Fechar', { duration: 3000 });
          this.carregarEmpilhadeiras();
          this.carregarResumo();
        },
        error: () => this.snackBar.open('Erro ao desbloquear', 'Fechar', { duration: 3000 })
      });
  }

  logout(): void { this.authService.logout(); }

  // Controle de permissões por perfil
  podeVerTodasEmpilhadeiras(): boolean {
    return this.perfil === Perfil.ADMIN || this.perfil === Perfil.GERENTE_MECANICA;
  }

  podeVerChecklists(): boolean {
    return this.perfil === Perfil.ADMIN
        || this.perfil === Perfil.SUPERVISOR_OPERACIONAL
        || this.perfil === Perfil.SUPERVISOR;
  }

  podeDesbloquear(): boolean {
    return this.perfil === Perfil.ADMIN || this.perfil === Perfil.GERENTE_MECANICA;
  }

  get labelPerfil(): string {
    const labels: Record<string, string> = {
      ADMIN: 'Administrador', GERENTE_MECANICA: 'Gerente de Mecânica',
      SUPERVISOR_OPERACIONAL: 'Supervisor Operacional', SUPERVISOR: 'Supervisor'
    };
    return labels[this.perfil ?? ''] ?? this.perfil ?? '';
  }

  get statusColor(): (s: string) => string {
    return (s) => s === 'DISPONIVEL' ? 'disponivel' : s === 'BLOQUEADA' ? 'bloqueada' : 'inativa';
  }

  get turnoColor(): (t: string) => string {
    return (t) => t.includes('Manhã') || t === 'A' ? 'manha' : t.includes('Tarde') || t === 'B' ? 'tarde' : 'noite';
  }
}
