export interface SettingsRequestDto {
  theme: string;
  language: string;
  emailNotifications: boolean;
  preferredModelKey: string;
  defaultSearchLimit: number;
  defaultMinScore: number;
}

export interface SettingsResponseDto {
  id: number;
  userId: number;
  theme: string;
  language: string;
  emailNotifications: boolean;
  preferredModelKey: string;
  defaultSearchLimit: number;
  defaultMinScore: number;
}
