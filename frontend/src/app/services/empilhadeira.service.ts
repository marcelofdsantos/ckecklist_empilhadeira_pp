import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  EmpilhadeiraResponse,
  EmpilhadeiraRequest,
  BloquearEmpilhadeiraRequest
} from '../models/api.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EmpilhadeiraService {
  private readonly API_URL = `${environment.apiUrl}/empilhadeiras`;

  constructor(private http: HttpClient) {}

  listarTodas(): Observable<EmpilhadeiraResponse[]> {
    return this.http.get<EmpilhadeiraResponse[]>(this.API_URL);
  }

  listarAtivas(): Observable<EmpilhadeiraResponse[]> {
    return this.http.get<EmpilhadeiraResponse[]>(`${this.API_URL}/ativas`);
  }

  listarDisponiveis(): Observable<EmpilhadeiraResponse[]> {
    return this.http.get<EmpilhadeiraResponse[]>(`${this.API_URL}/disponiveis`);
  }

  listarBloqueadas(): Observable<EmpilhadeiraResponse[]> {
    return this.http.get<EmpilhadeiraResponse[]>(`${this.API_URL}/bloqueadas`);
  }

  buscarPorId(id: number): Observable<EmpilhadeiraResponse> {
    return this.http.get<EmpilhadeiraResponse>(`${this.API_URL}/${id}`);
  }

  criar(request: EmpilhadeiraRequest): Observable<EmpilhadeiraResponse> {
    return this.http.post<EmpilhadeiraResponse>(this.API_URL, request);
  }

  bloquear(id: number, request: BloquearEmpilhadeiraRequest): Observable<EmpilhadeiraResponse> {
    return this.http.patch<EmpilhadeiraResponse>(`${this.API_URL}/${id}/bloquear`, request);
  }

  desbloquear(id: number): Observable<EmpilhadeiraResponse> {
    return this.http.patch<EmpilhadeiraResponse>(`${this.API_URL}/${id}/desbloquear`, {});
  }

  inativar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }
}
