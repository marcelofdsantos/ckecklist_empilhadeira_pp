import { TestBed } from '@angular/core/testing';
import { Checklist } from './checklist';

describe('Checklist', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Checklist]
    }).compileComponents();
  });

  it('deve criar o checklist', () => {
    const fixture = TestBed.createComponent(Checklist);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
