// ========== AUTH MODELS ==========
export interface LoginRequest {
  re: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  usuarioId: number;
  re: string;
  nomeCompleto: string;
  perfil: Perfil;
}

// ========== ENUMS ==========
export enum Perfil {
  OPERADOR             = 'OPERADOR',
  SUPERVISOR           = 'SUPERVISOR',
  SUPERVISOR_OPERACIONAL = 'SUPERVISOR_OPERACIONAL',
  GERENTE_MECANICA     = 'GERENTE_MECANICA',
  ADMIN                = 'ADMIN'
}

export enum Turno {
  A = 'A', B = 'B', C = 'C'
}

export enum TipoItem {
  CONFORME   = 'CONFORME',
  IMPEDITIVO = 'IMPEDITIVO'
}

export enum StatusItem {
  OK           = 'OK',
  NAO_CONFORME = 'NAO_CONFORME'
}

export enum ResultadoChecklist {
  APROVADO  = 'APROVADO',
  REPROVADO = 'REPROVADO'
}

// ========== USUARIO ==========
export interface UsuarioResponse {
  id: number;
  re: string;
  nomeCompleto: string;
  perfil: Perfil;
  ativo: boolean;
}

export interface UsuarioRequest {
  re: string;
  nomeCompleto: string;
  senha: string;
  perfil: Perfil;
}

// ========== EMPILHADEIRA ==========
export interface EmpilhadeiraResponse {
  id: number;
  modelo: string;
  tipo: string;
  capacidade: number;
  bloqueada: boolean;
  motivoBloqueio?: string;
  ativa: boolean;
}

export interface EmpilhadeiraRequest {
  modelo: string;
  tipo: string;
  capacidade: number;
}

export interface BloquearEmpilhadeiraRequest {
  motivo: string;
}

// ========== CHECKLIST ==========
export interface ChecklistRequest {
  data: string;
  horaVistoria: string;
  turno: Turno;
  horimetroInicial: number;
  horimetroFinal?: number;
  operadorId: number;
  empilhadeiraId: number;
  itens: ItemChecklistRequest[];
  observacaoGeral?: string;
}

export interface ItemChecklistRequest {
  descricao: string;
  tipo: TipoItem;
  status: StatusItem;
  observacao?: string;
}

export interface ChecklistResponse {
  id: number;
  data: string;
  diaSemana: string;
  horaVistoria: string;
  turno: Turno;
  horimetroInicial: number;
  horimetroFinal?: number;
  operador: UsuarioResponse;
  empilhadeira: EmpilhadeiraResponse;
  resultado: ResultadoChecklist;
  itens: ItemChecklistResponse[];
  observacaoGeral?: string;
}

export interface ItemChecklistResponse {
  id: number;
  descricao: string;
  tipo: TipoItem;
  status: StatusItem;
  observacao?: string;
}

// ========== DASHBOARD ==========
export interface DashboardResumo {
  totalEmpilhadeiras: number;
  empilhadeirasDisponiveis: number;
  empilhadeirasBloqueadas: number;
  empilhadeirasInativas: number;
  checklistsHoje: number;
  checklistsAprovadosHoje: number;
  checklistsReprovadosHoje: number;
  operadoresAtivos: number;
}

export interface EmpilhadeiraStatus {
  id: number;
  modelo: string;
  tipo: string;
  capacidade: number;
  status: 'DISPONIVEL' | 'BLOQUEADA' | 'INATIVA';
  motivoBloqueio?: string;
  atualizadoEm?: string;
  ultimoChecklistData?: string;
  ultimoChecklistResultado?: string;
  ultimoOperador?: string;
}

export interface ChecklistRecente {
  id: number;
  empilhadeiraModelo: string;
  operadorNome: string;
  data: string;
  horaVistoria: string;
  turno: string;
  resultado: string;
}

export interface ChecklistPorTurno {
  turno: string;
  aprovados: number;
  reprovados: number;
  total: number;
}

// ========== ERROR RESPONSE ==========
export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  details?: { [key: string]: string };
}

// ========== HELPERS ==========
export const PERFIS_PAINEL = [
  Perfil.ADMIN,
  Perfil.GERENTE_MECANICA,
  Perfil.SUPERVISOR_OPERACIONAL,
  Perfil.SUPERVISOR
];

export function podeAcessarPainel(perfil: Perfil): boolean {
  return PERFIS_PAINEL.includes(perfil);
}
