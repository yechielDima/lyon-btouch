import { api } from './client'
import type {
  ScheduleWeekResponse,
  ShiftResponse,
  ShiftRequirementResponse,
  ShiftRequirementRequest,
  ScaffoldWeekRequest,
} from './types'

export const scheduleApi = {
  getAllWeeks: () => {
    return api.get<ScheduleWeekResponse[]>('/schedule-weeks')
  },
  
  getWeek: (weekId: number) => {
    return api.get<ScheduleWeekResponse>(`/schedule-weeks/${weekId}`)
  },

  scaffoldWeek: (request: ScaffoldWeekRequest) => {
    return api.post<ScheduleWeekResponse>('/schedule-weeks/scaffold', request)
  },

  openWeek: (weekId: number) => {
    return api.put<ScheduleWeekResponse>(`/schedule-weeks/${weekId}/open`)
  },

  getShiftsForWeek: (weekId: number) => {
    return api.get<ShiftResponse[]>(`/schedule-weeks/${weekId}/shifts`)
  },

  deleteShift: (shiftId: number) => {
    return api.delete<void>(`/shifts/${shiftId}`)
  },

  getShiftRequirements: (shiftId: number) => {
    return api.get<ShiftRequirementResponse[]>(`/shifts/${shiftId}/requirements`)
  },

  setShiftRequirement: (shiftId: number, request: ShiftRequirementRequest) => {
    return api.post<ShiftRequirementResponse>(`/shifts/${shiftId}/requirements`, request)
  },
}
