import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatRadioModule } from '@angular/material/radio';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';

import { ChecklistService } from '../../services/checklist.service';
import { EmpilhadeiraService } from '../../services/empilhadeira.service';
import { AuthService } from '../../services/auth.service';
import { podeAcessarPainel } from '../../models/api.models';
import {
  ChecklistRequest,
  ItemChecklistRequest,
  EmpilhadeiraResponse,
  TipoItem,
  StatusItem,
  Turno
} from '../../models/api.models';

@Component({
  selector: 'app-checklist',
  standalone: true,
  templateUrl: './checklist.html',
  styleUrls: ['./checklist.css'],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatRadioModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatSnackBarModule,
    MatIconModule,
    MatDividerModule
  ]
})
export class Checklist implements OnInit, OnDestroy {

  form!: FormGroup;
  carregando = false;
  salvando = false;

  empilhadeirasDisponiveis: EmpilhadeiraResponse[] = [];
  nomeOperador = '';
  reOperador = '';

  // BUG FIX: Subject para cancelar subscriptions no destroy (evita memory leak)
  private destroy$ = new Subject<void>();

  itensConformes = [
    'Gotejamento',
    'Pneus dianteiro',
    'Pneus traseiro',
    'Garfos',
    'Limpeza'
  ];

  itensImpeditivos = [
    'Direção',
    'Cinto de segurança',
    'Extintor de incêndio',
    'Buzina',
    'Vazamento',
    'Freio pedal',
    'Freio de estacionário',
    'Espelho retrovisor',
    'Sirene de ré',
    'Iluminação ou sinalização',
    'Pinos da patola',
    'Painel ou alavancas inoperante',
    'Giroflex',
    'Fixação do cilindro de GLP ineficiente',
    'Nível do óleo do motor',
    'Água do radiador'
  ];

  turnos = [
    { value: Turno.A, label: 'Turno A - Manhã' },
    { value: Turno.B, label: 'Turno B - Tarde' },
    { value: Turno.C, label: 'Turno C - Noite' }
  ];

  constructor(
    private fb: FormBuilder,
    private checklistService: ChecklistService,
    private empilhadeiraService: EmpilhadeiraService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarDadosUsuario();
    this.carregarEmpilhadeiras();
    this.inicializarFormulario();
  }

  // BUG FIX: cancela subscriptions ao destruir o componente
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  carregarDadosUsuario(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.nomeOperador = user.nomeCompleto;
      this.reOperador = user.re;
    }
  }

  carregarEmpilhadeiras(): void {
    this.carregando = true;
    this.empilhadeiraService.listarDisponiveis()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (empilhadeiras) => {
          this.empilhadeirasDisponiveis = empilhadeiras;
          this.carregando = false;
        },
        error: () => {
          this.snackBar.open('❌ Erro ao carregar empilhadeiras', 'Fechar', { duration: 3000 });
          this.carregando = false;
        }
      });
  }

  inicializarFormulario(): void {
    const now = new Date();
    const dataFormatada = now.toISOString().split('T')[0];
    const horaFormatada = now.toTimeString().substring(0, 8); // HH:mm:ss

    this.form = this.fb.group({
      empilhadeiraId: ['', Validators.required],
      data: [dataFormatada, Validators.required],
      horaVistoria: [horaFormatada, Validators.required],
      turno: [Turno.A, Validators.required],
      horimetroInicial: [null, [Validators.required, Validators.min(0)]],
      horimetroFinal: [null, Validators.min(0)],
      observacaoGeral: [''],
      conformes: this.fb.array([]),
      impeditivos: this.fb.array([])
    });

    this.criarItens();
  }

  criarItens(): void {
    this.itensConformes.forEach(nome =>
      this.conformes.push(this.criarItem(nome, TipoItem.CONFORME))
    );
    this.itensImpeditivos.forEach(nome =>
      this.impeditivos.push(this.criarItem(nome, TipoItem.IMPEDITIVO))
    );
  }

  criarItem(nome: string, tipo: TipoItem): FormGroup {
    return this.fb.group({
      nome: [nome],
      tipo: [tipo],
      status: [StatusItem.OK],
      observacao: ['']
    });
  }

  get conformes(): FormArray {
    return this.form.get('conformes') as FormArray;
  }

  get impeditivos(): FormArray {
    return this.form.get('impeditivos') as FormArray;
  }

  existeImpeditivoNaoConforme(): boolean {
    return this.impeditivos.value.some(
      (item: any) => item.status === StatusItem.NAO_CONFORME
    );
  }

  logout(): void {
    this.authService.logout();
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.snackBar.open('⚠️ Preencha todos os campos obrigatórios', 'Fechar', { duration: 3000 });
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      this.snackBar.open('❌ Sessão expirada, faça login novamente', 'Fechar', { duration: 3000 });
      this.router.navigate(['/login']);
      return;
    }

    if (this.existeImpeditivoNaoConforme()) {
      const confirma = confirm(
        '⚠️ ATENÇÃO! Existem itens impeditivos não conformes.\n\n' +
        '🚫 A empilhadeira será BLOQUEADA automaticamente.\n\n' +
        'Deseja continuar?'
      );
      if (!confirma) return;
    }

    const itens: ItemChecklistRequest[] = [
      ...this.conformes.value,
      ...this.impeditivos.value
    ].map((item: any) => ({
      descricao: item.nome,
      tipo: item.tipo,
      status: item.status,
      observacao: item.observacao || undefined
    }));

    const horimetroInicialVal = this.form.value.horimetroInicial;
    const horimetroFinalVal = this.form.value.horimetroFinal;

    // BUG FIX: parseInt com radix 10 explícito e verificação de NaN
    const horimetroInicial = parseInt(String(horimetroInicialVal), 10);
    const horimetroFinal = horimetroFinalVal !== null && horimetroFinalVal !== ''
      ? parseInt(String(horimetroFinalVal), 10)
      : undefined;

    if (isNaN(horimetroInicial)) {
      this.snackBar.open('⚠️ Horímetro inicial inválido', 'Fechar', { duration: 3000 });
      return;
    }

    const request: ChecklistRequest = {
      data: this.form.value.data,
      horaVistoria: this.form.value.horaVistoria,
      turno: this.form.value.turno,
      horimetroInicial,
      horimetroFinal,
      operadorId: user.usuarioId,
      empilhadeiraId: parseInt(String(this.form.value.empilhadeiraId), 10),
      itens,
      observacaoGeral: this.form.value.observacaoGeral || undefined
    };

    this.salvando = true;

    this.checklistService.criar(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.salvando = false;

          if (response.resultado === 'REPROVADO') {
            this.snackBar.open(
              '🚫 Checklist REPROVADO! Empilhadeira bloqueada automaticamente.',
              'Fechar',
              { duration: 5000 }
            );
          } else {
            this.snackBar.open(
              '✅ Checklist salvo com sucesso!',
              'Fechar',
              { duration: 3000 }
            );
          }

          this.inicializarFormulario();
          this.carregarEmpilhadeiras();
        },
        error: (error) => {
          this.salvando = false;
          const mensagem = error.error?.message || 'Erro ao salvar checklist';
          this.snackBar.open(`❌ ${mensagem}`, 'Fechar', { duration: 5000 });
        }
      });
  }
}
