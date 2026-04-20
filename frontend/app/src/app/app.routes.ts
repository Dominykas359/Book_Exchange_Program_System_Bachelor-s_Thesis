import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'login' },

  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then(m => m.Register)
  },

  {
    path: '',
    loadComponent: () => import('./layout/app-shell/app-shell').then(m => m.AppShell),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard').then(m => m.Dashboard)
      },
      {
        path: 'create',
        loadComponent: () => import('./features/create-notice-page/create-notice-page').then(m => m.CreateNoticePageComponent)
      },
      {
        path: 'ai-agent',
        loadComponent: () => import('./features/ai-agent-page/ai-agent-page').then(m => m.AiAgentPage)
      },
      {
        path: 'history',
        loadComponent: () => import('./features/history-page/history-page').then(m => m.HistoryPage)
      },
      {
        path: 'exchange-requests',
        loadComponent: () => import('./features/exchange-requests-page/exchange-requests-page').then(m => m.ExchangeRequestsPage)
      },
      {
        path: 'exchange-requests/create',
        loadComponent: () => import('./features/exchange-request-create-page/exchange-request-create-page').then(m => m.ExchangeRequestCreatePage)
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/profile-page/profile-page').then(m => m.ProfilePage)
      },
      {
        path: 'settings',
        loadComponent: () => import('./features/settings-page/settings-page').then(m => m.SettingsPage)
      }
    ]
  },

  { path: '**', redirectTo: 'login' }
];
