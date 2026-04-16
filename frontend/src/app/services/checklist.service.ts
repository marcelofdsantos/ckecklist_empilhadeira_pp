import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChecklistRequest, ChecklistResponse } from '../models/api.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ChecklistService {
  private readonly API_URL = `${environment.apiUrl}/checklists`;

  constructor(private http: HttpClient) {}

  criar(request: ChecklistRequest): Observable<ChecklistResponse> {
    return this.http.post<ChecklistResponse>(this.API_URL, request);
  }

  listarTodos(): Observable<ChecklistResponse[]> {
    return this.http.get<ChecklistResponse[]>(this.API_URL);
  }

  buscarPorId(id: number): Observable<ChecklistResponse> {
    return this.http.get<ChecklistResponse>(`${this.API_URL}/${id}`);
  }

  listarPorEmpilhadeira(empilhadeiraId: number): Observable<ChecklistResponse[]> {
    return this.http.get<ChecklistResponse[]>(`${this.API_URL}/empilhadeira/${empilhadeiraId}`);
  }

  listarPorOperador(operadorId: number): Observable<ChecklistResponse[]> {
    return this.http.get<ChecklistResponse[]>(`${this.API_URL}/operador/${operadorId}`);
  }

  listarPorData(data: string): Observable<ChecklistResponse[]> {
    return this.http.get<ChecklistResponse[]>(`${this.API_URL}/data/${data}`);
  }

  listarPorPeriodo(dataInicio: string, dataFim: string): Observable<ChecklistResponse[]> {
    const params = new HttpParams()
      .set('dataInicio', dataInicio)
      .set('dataFim', dataFim);
    return this.http.get<ChecklistResponse[]>(`${this.API_URL}/periodo`, { params });
  }
}
