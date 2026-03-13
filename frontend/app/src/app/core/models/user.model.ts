export interface UserResponseDto {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
}

export interface AuthenticationResponseDto extends UserResponseDto {
  token: string;
}

export interface UserUpdateRequestDto {
  email: string;
  firstName: string;
  lastName: string;
}

export interface ChangePasswordRequestDto {
  password: string;
}
