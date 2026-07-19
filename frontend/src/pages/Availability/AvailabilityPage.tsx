import { useState, useEffect } from 'react'
import { scheduleApi } from '../../api/schedule'
import { ScheduleWeekResponse, ShiftResponse, AvailabilityResponse, WeekStatus, ShiftType, SystemRole } from '../../api/types'
import { useAuth } from '../../context/AuthContext'

const shiftTypeLabels: Record<ShiftType, string> = {
  [ShiftType.MORNING]: 'בוקר',
  [ShiftType.EVENING]: 'ערב',
  [ShiftType.FRIDAY]: 'שישי',
  [ShiftType.MOTZASH]: 'מוצ"ש',
}

export default function AvailabilityPage() {
  const { user } = useAuth()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [openWeek, setOpenWeek] = useState<ScheduleWeekResponse | null>(null)
  const [shifts, setShifts] = useState<ShiftResponse[]>([])
  const [availabilities, setAvailabilities] = useState<AvailabilityResponse[]>([])
  const [actionLoading, setActionLoading] = useState<number | null>(null) // track shiftId currently loading

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    if (!user) return
    try {
      setLoading(true)
      setError('')
      const weeks = await scheduleApi.getAllWeeks()
      const open = weeks.find(w => w.status === WeekStatus.OPEN_FOR_SUBMISSION)
      if (open) {
        setOpenWeek(open)
        const weekShifts = await scheduleApi.getShiftsForWeek(open.id)
        setShifts(weekShifts)
        const userAvail = await scheduleApi.getUserAvailability(user.id)
        setAvailabilities(userAvail)
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בטעינת הנתונים')
    } finally {
      setLoading(false)
    }
  }

  const handleToggle = async (shiftId: number, isAvailable: boolean) => {
    try {
      setError('')
      setActionLoading(shiftId)
      if (isAvailable) {
        await scheduleApi.removeAvailability(shiftId)
        setAvailabilities(prev => prev.filter(a => a.shiftId !== shiftId))
      } else {
        const newAvail = await scheduleApi.submitAvailability({ shiftId })
        setAvailabilities(prev => [...prev, newAvail])
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בעדכון הזמינות')
      // Refresh to ensure sync with backend in case of error
      await loadData()
    } finally {
      setActionLoading(null)
    }
  }

  // Group shifts by date
  const groupedShifts = shifts.reduce((acc, shift) => {
    if (!acc[shift.shiftDate]) {
      acc[shift.shiftDate] = []
    }
    acc[shift.shiftDate].push(shift)
    return acc
  }, {} as Record<string, ShiftResponse[]>)

  // Sort dates
  const sortedDates = Object.keys(groupedShifts).sort()

  const isShiftManager = user?.systemRole === SystemRole.SHIFT_MANAGER

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center">
        <div className="text-white text-xl">טוען...</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-slate-900 p-4 pb-20 md:p-8" dir="rtl">
      <div className="max-w-2xl mx-auto">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold text-white mb-2">הגשת זמינות</h1>
          <p className="text-slate-400">
            סמן את המשמרות בהן אתה פנוי לעבוד
          </p>
        </div>

        {error && (
          <div className="bg-red-500/20 border border-red-500/50 text-red-200 p-4 rounded-xl mb-6 text-center">
            {error}
          </div>
        )}

        {!openWeek ? (
          <div className="bg-white/5 border border-white/10 rounded-2xl p-12 text-center">
            <h2 className="text-2xl font-bold text-slate-300">אין שבוע פתוח להגשת זמינות כרגע</h2>
          </div>
        ) : (
          <div>
            {isShiftManager && (
              <div className="bg-blue-500/20 border border-blue-500/50 text-blue-200 p-4 rounded-xl mb-6 text-center">
                כמנהל משמרת, אינך צריך להגיש זמינות. אתה משובץ ישירות על ידי מנהל המערכת.
              </div>
            )}

            <div className="space-y-6">
              {sortedDates.map(dateStr => {
                const dateObj = new Date(dateStr)
                const dayLabel = dateObj.toLocaleDateString('he-IL', { weekday: 'long', day: 'numeric', month: 'long' })
                
                return (
                  <div key={dateStr} className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden">
                    <div className="bg-black/20 p-4 border-b border-white/10">
                      <h3 className="text-lg font-bold text-white">{dayLabel}</h3>
                    </div>
                    <div className="p-4 space-y-4">
                      {groupedShifts[dateStr].map(shift => {
                        const isAvailable = availabilities.some(a => a.shiftId === shift.id)
                        const isLoading = actionLoading === shift.id
                        
                        return (
                          <div key={shift.id} className="flex items-center justify-between p-4 bg-slate-800/50 rounded-xl border border-white/5">
                            <div>
                              <div className="text-white font-semibold text-lg">{shiftTypeLabels[shift.shiftType]}</div>
                            </div>
                            
                            {!isShiftManager && (
                              <button
                                onClick={() => handleToggle(shift.id, isAvailable)}
                                disabled={isLoading}
                                className={`
                                  relative overflow-hidden transition-all duration-300 px-6 py-2 rounded-xl font-bold text-sm
                                  ${isLoading ? 'opacity-50 cursor-not-allowed' : ''}
                                  ${isAvailable 
                                    ? 'bg-emerald-500 hover:bg-emerald-600 text-white shadow-[0_0_20px_rgba(16,185,129,0.3)]' 
                                    : 'bg-white/10 hover:bg-white/20 text-slate-300'
                                  }
                                `}
                              >
                                {isLoading ? 'מעדכן...' : isAvailable ? 'פנוי' : 'לא פנוי'}
                              </button>
                            )}
                          </div>
                        )
                      })}
                    </div>
                  </div>
                )
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
