import { useState, useEffect } from 'react'
import { scheduleApi } from '../../api/schedule'
import type { ScheduleWeekResponse, ShiftResponse } from '../../api/types'
import { WeekStatus } from '../../api/types'
import ShiftCard from './components/ShiftCard'

export default function ScheduleBuilderPage() {
  const [weeks, setWeeks] = useState<ScheduleWeekResponse[]>([])
  const [selectedWeekId, setSelectedWeekId] = useState<number | null>(null)
  const [shifts, setShifts] = useState<ShiftResponse[]>([])
  
  const [newWeekDate, setNewWeekDate] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionLoading, setActionLoading] = useState(false)

  const fetchWeeks = async () => {
    try {
      setLoading(true)
      const data = await scheduleApi.getAllWeeks()
      data.sort((a, b) => new Date(b.weekStartDate).getTime() - new Date(a.weekStartDate).getTime())
      setWeeks(data)
      if (data.length > 0 && !selectedWeekId) {
        setSelectedWeekId(data[0].id)
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בטעינת שבועות')
    } finally {
      setLoading(false)
    }
  }

  const fetchShifts = async (weekId: number) => {
    try {
      const data = await scheduleApi.getShiftsForWeek(weekId)
      data.sort((a, b) => {
        const d = new Date(a.shiftDate).getTime() - new Date(b.shiftDate).getTime()
        if (d !== 0) return d
        return a.shiftType.localeCompare(b.shiftType)
      })
      setShifts(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בטעינת משמרות')
    }
  }

  useEffect(() => {
    fetchWeeks()
  }, [])

  useEffect(() => {
    if (selectedWeekId) {
      fetchShifts(selectedWeekId)
    } else {
      setShifts([])
    }
  }, [selectedWeekId])

  const handleScaffold = async () => {
    if (!newWeekDate) {
      setError('אנא בחר תאריך התחלה (יום ראשון)')
      return
    }
    const d = new Date(newWeekDate)
    if (d.getDay() !== 0) {
      setError('תאריך פתיחת השבוע חייב להיות יום ראשון')
      return
    }

    try {
      setError('')
      setActionLoading(true)
      const week = await scheduleApi.scaffoldWeek({ weekStartDate: newWeekDate })
      setWeeks(prev => [week, ...prev].sort((a, b) => new Date(b.weekStartDate).getTime() - new Date(a.weekStartDate).getTime()))
      setSelectedWeekId(week.id)
      setNewWeekDate('')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בבניית שבוע חדש')
    } finally {
      setActionLoading(false)
    }
  }

  const handleOpenWeek = async () => {
    if (!selectedWeekId) return
    try {
      setError('')
      setActionLoading(true)
      const week = await scheduleApi.openWeek(selectedWeekId)
      setWeeks(prev => prev.map(w => w.id === week.id ? week : w))
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בפתיחת השבוע')
    } finally {
      setActionLoading(false)
    }
  }

  const handleDeleteShift = (shiftId: number) => {
    setShifts(prev => prev.filter(s => s.id !== shiftId))
  }

  if (loading && weeks.length === 0) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4" dir="rtl">
        <div className="text-emerald-500 text-xl">טוען...</div>
      </div>
    )
  }

  const selectedWeek = weeks.find(w => w.id === selectedWeekId)

  return (
    <div className="min-h-screen bg-gradient-to-bl from-slate-900 via-slate-800 to-slate-900 p-4 md:p-8" dir="rtl">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-white mb-8">ניהול סידורי עבודה</h1>
        
        {error && (
          <div className="bg-red-500/20 border border-red-500/50 text-red-200 p-4 rounded-xl mb-6">
            {error}
          </div>
        )}

        <div className="grid gap-6 md:grid-cols-3 mb-8">
          <div className="md:col-span-2 bg-white/5 border border-white/10 rounded-2xl p-6">
            <h2 className="text-xl font-bold text-white mb-4">בחר שבוע לעריכה</h2>
            <select
              value={selectedWeekId || ''}
              onChange={(e) => setSelectedWeekId(Number(e.target.value))}
              className="w-full bg-slate-800/50 border border-white/10 rounded-xl px-4 py-2.5 text-white focus:outline-none focus:border-emerald-500 transition-colors"
            >
              <option value="" disabled>בחר שבוע...</option>
              {weeks.map(w => (
                <option key={w.id} value={w.id}>
                  שבוע שמתחיל ב- {new Date(w.weekStartDate).toLocaleDateString('he-IL')} 
                  ({w.status === WeekStatus.DRAFT ? 'טיוטה' : w.status === WeekStatus.OPEN_FOR_SUBMISSION ? 'פתוח להגשות' : 'מפורסם'})
                </option>
              ))}
            </select>
          </div>

          <div className="bg-white/5 border border-white/10 rounded-2xl p-6">
            <h2 className="text-xl font-bold text-white mb-4">בניית שבוע חדש</h2>
            <div className="space-y-3">
              <input
                type="date"
                value={newWeekDate}
                onChange={(e) => setNewWeekDate(e.target.value)}
                className="w-full bg-slate-800/50 border border-white/10 rounded-xl px-4 py-2.5 text-white focus:outline-none focus:border-emerald-500 transition-colors"
              />
              <button
                onClick={handleScaffold}
                disabled={actionLoading || !newWeekDate}
                className="w-full bg-emerald-500 hover:bg-emerald-600 text-white font-semibold py-2.5 px-4 rounded-xl transition-colors disabled:opacity-50"
              >
                בנה שבוע
              </button>
            </div>
          </div>
        </div>

        {selectedWeek && (
          <div className="bg-white/5 border border-white/10 rounded-2xl p-6 mb-8">
            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center mb-6">
              <div>
                <h2 className="text-2xl font-bold text-white">
                  משמרות לשבוע {new Date(selectedWeek.weekStartDate).toLocaleDateString('he-IL')}
                </h2>
                <p className="text-slate-400 mt-1">סטטוס נוכחי: <span className="text-white font-medium">{
                  selectedWeek.status === WeekStatus.DRAFT ? 'טיוטה' : 
                  selectedWeek.status === WeekStatus.OPEN_FOR_SUBMISSION ? 'פתוח להגשות' : 'מפורסם'
                }</span></p>
              </div>

              {selectedWeek.status === WeekStatus.DRAFT && (
                <button
                  onClick={handleOpenWeek}
                  disabled={actionLoading || shifts.length === 0}
                  className="mt-4 sm:mt-0 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2.5 px-6 rounded-xl transition-colors disabled:opacity-50"
                >
                  פתח שבוע להגשות
                </button>
              )}
            </div>

            <div className="space-y-4">
              {shifts.length === 0 ? (
                <p className="text-slate-400 text-center py-8">אין משמרות לשבוע זה.</p>
              ) : (
                shifts.map(shift => (
                  <ShiftCard
                    key={shift.id}
                    shift={shift}
                    weekStatus={selectedWeek.status}
                    onDelete={handleDeleteShift}
                  />
                ))
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
