import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  DashboardResumo,
  EmpilhadeiraStatus,
  ChecklistRecente,
  ChecklistPorTurno
} from '../models/api.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private readonly API = `${environment.apiUrl}/dashboard`;

  constructor(private http: HttpClient) {}

  getResumo(): Observable<DashboardResumo> {
    return this.http.get<DashboardResumo>(`${this.API}/resumo`);
  }

  getStatusEmpilhadeiras(): Observable<EmpilhadeiraStatus[]> {
    return this.http.get<EmpilhadeiraStatus[]>(`${this.API}/empilhadeiras/status`);
  }

  getBloqueadas(): Observable<EmpilhadeiraStatus[]> {
    return this.http.get<EmpilhadeiraStatus[]>(`${this.API}/empilhadeiras/bloqueadas`);
  }

  getChecklistsRecentes(limite = 20): Observable<ChecklistRecente[]> {
    return this.http.get<ChecklistRecente[]>(`${this.API}/checklists/recentes?limite=${limite}`);
  }

  getChecklistsPorTurno(data?: string): Observable<ChecklistPorTurno[]> {
    const url = data ? `${this.API}/checklists/por-turno?data=${data}` : `${this.API}/checklists/por-turno`;
    return this.http.get<ChecklistPorTurno[]>(url);
  }

  /** Cria uma conexão SSE para receber eventos em tempo real */
  conectarEventos(): EventSource {
    const token = sessionStorage.getItem('auth_token');
    // EventSource não suporta headers; passamos o token via query string
    // O backend valida por query param apenas neste endpoint
    return new EventSource(`${environment.apiUrl}/dashboard/eventos?token=${token}`);
  }
}
