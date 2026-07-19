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
  HOSTESS = 'HOSTESS',
  COOK = 'COOK',
}

export enum WeekStatus {
  DRAFT = 'DRAFT',
  OPEN_FOR_SUBMISSION = 'OPEN_FOR_SUBMISSION',
  PUBLISHED = 'PUBLISHED',
}

export enum ShiftType {
  MORNING = 'MORNING',
  EVENING = 'EVENING',
  FRIDAY = 'FRIDAY',
  MOTZASH = 'MOTZASH',
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

export interface ApproveUserRequest {
  systemRole: SystemRole
  qualifications: string[]
  isChecker: boolean
}

export interface ScheduleWeekResponse {
  id: number
  weekStartDate: string
  status: WeekStatus
  publishedAt: string | null
  generatedAt: string | null
  generatedByUserId: number | null
  createdAt: string
}

export interface ShiftResponse {
  id: number
  weekId: number
  shiftDate: string
  shiftType: ShiftType
  shiftManagerId: number | null
}

export interface ShiftRequirementResponse {
  id: number
  shiftId: number
  positionCode: PositionCode
  requiredCount: number
}

export interface ShiftRequirementRequest {
  positionCode: PositionCode
  requiredCount: number
}

export interface ScaffoldWeekRequest {
  weekStartDate: string
}

export interface AvailabilityResponse {
  id: number
  userId: number
  shiftId: number
}

export interface AvailabilityRequest {
  shiftId: number
}

export interface ApiError {
  message: string
  status: number
}
