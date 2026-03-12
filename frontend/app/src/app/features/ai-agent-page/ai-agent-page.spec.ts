import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AiAgentPage } from './ai-agent-page';

describe('AiAgentPage', () => {
  let component: AiAgentPage;
  let fixture: ComponentFixture<AiAgentPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AiAgentPage],
    }).compileComponents();

    fixture = TestBed.createComponent(AiAgentPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
