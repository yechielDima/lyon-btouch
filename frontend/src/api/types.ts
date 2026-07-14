export enum SystemRole {
  EMPLOYEE = 'EMPLOYEE',
  SHIFT_MANAGER = 'SHIFT_MANAGER',
  MANAGER = 'MANAGER',
}

export enum AccountStatus {
  PENDING = 'PENDING',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
}

export enum PositionCode {
  WAITER = 'WAITER',
  BARTENDER = 'BARTENDER',
  HOST = 'HOST',
  COOK = 'COOK',
}

export interface User {
  id: number
  fullName: string
  phone: string
  systemRole: SystemRole
  accountStatus: AccountStatus
  checker: boolean
  reliabilityScore: number
  active: boolean
  qualifications: string[]
  createdAt: string
}

export interface RegisterRequest {
  fullName: string
  phone: string
}

export interface RequestCodeRequest {
  phone: string
}

export interface VerifyCodeRequest {
  phone: string
  code: string
}

export interface UserResponse extends User {}

export interface ApiError {
  message: string
  status: number
}
