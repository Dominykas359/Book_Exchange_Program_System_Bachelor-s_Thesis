import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoticeModal } from './notice-modal';

describe('NoticeModal', () => {
  let component: NoticeModal;
  let fixture: ComponentFixture<NoticeModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NoticeModal],
    }).compileComponents();

    fixture = TestBed.createComponent(NoticeModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
