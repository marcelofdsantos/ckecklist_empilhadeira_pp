import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { AuthService } from '../../services/auth.service';
import { podeAcessarPainel } from '../../models/api.models';
import { LoginRequest } from '../../models/api.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent implements OnInit, AfterViewInit {
  loginForm!: FormGroup;
  carregando = false;

  @ViewChild('reInput')    reInput!:    ElementRef<HTMLInputElement>;
  @ViewChild('senhaInput') senhaInput!: ElementRef<HTMLInputElement>;
  @ViewChild('btnEntrar')  btnEntrar!:  ElementRef<HTMLButtonElement>;
  @ViewChild('logoVideo')  logoVideo!:  ElementRef<HTMLVideoElement>;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    this.loginForm = this.fb.group({
      re:    ['', [Validators.required]],
      senha: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      const user = this.authService.getCurrentUser();
      const dest = user && podeAcessarPainel(user.perfil) ? '/admin' : '/checklist';
      this.router.navigate([dest]);
    }
  }

  ngAfterViewInit(): void {
    const video = this.logoVideo?.nativeElement;
    if (video) {
      video.muted = true;
      video.currentTime = 0;
      setTimeout(() => video.play().catch(() => {}), 300);
      setInterval(() => { video.currentTime = 0; video.play(); }, 7000);
    }
    setTimeout(() => this.reInput?.nativeElement.focus(), 500);
  }

  irParaSenha(): void {
    if (this.loginForm.get('re')?.valid) {
      this.senhaInput.nativeElement.focus();
    }
  }

  irParaEntrar(): void {
    if (this.loginForm.valid && !this.carregando) {
      this.submit();
    }
  }

  submit(): void {
    if (this.loginForm.invalid || this.carregando) return;

    this.carregando = true;

    const loginRequest: LoginRequest = {
      re:    this.loginForm.value.re.trim(),
      senha: this.loginForm.value.senha
    };

    this.authService.login(loginRequest).subscribe({
      next: (response) => {
        this.carregando = false;
        // SEC-9: limpar campo senha da memória do form após login
        this.loginForm.get('senha')?.reset();
        this.snackBar.open(`✅ Bem-vindo, ${response.nomeCompleto}!`, 'Fechar', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
        const dest = podeAcessarPainel(response.perfil) ? '/admin' : '/checklist';
        this.router.navigate([dest]);
      },
      error: (error) => {
        this.carregando = false;
        // SEC-9: limpar senha em caso de erro também
        this.loginForm.get('senha')?.reset();
        const mensagem = error.status === 429
          ? 'Muitas tentativas. Aguarde 1 minuto.'
          : (error.error?.message || 'Credenciais inválidas.');
        this.snackBar.open(`❌ ${mensagem}`, 'Fechar', {
          duration: 5000,
          horizontalPosition: 'center',
          verticalPosition: 'top'
        });
      }
    });
  }
}
