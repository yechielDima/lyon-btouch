import { useState, useEffect } from 'react'
import type { ShiftResponse, ShiftRequirementResponse } from '../../../api/types'
import { ShiftType, PositionCode, WeekStatus } from '../../../api/types'
import { scheduleApi } from '../../../api/schedule'

interface ShiftCardProps {
  shift: ShiftResponse
  weekStatus: WeekStatus
  onDelete: (shiftId: number) => void
}

const shiftTypeLabels: Record<ShiftType, string> = {
  [ShiftType.MORNING]: 'בוקר',
  [ShiftType.EVENING]: 'ערב',
  [ShiftType.FRIDAY]: 'שישי',
  [ShiftType.MOTZASH]: 'מוצ"ש',
}

const positionLabels: Record<PositionCode, string> = {
  [PositionCode.WAITER]: 'מלצרים',
  [PositionCode.BARTENDER]: 'ברמנים',
  [PositionCode.HOSTESS]: 'מארחות',
  [PositionCode.COOK]: 'טבחים',
}

export default function ShiftCard({ shift, weekStatus, onDelete }: ShiftCardProps) {
  const [expanded, setExpanded] = useState(false)
  const [requirements, setRequirements] = useState<Record<PositionCode, number>>({
    [PositionCode.WAITER]: 0,
    [PositionCode.BARTENDER]: 0,
    [PositionCode.HOSTESS]: 0,
    [PositionCode.COOK]: 0,
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const isEditable = weekStatus !== WeekStatus.PUBLISHED

  useEffect(() => {
    if (expanded) {
      loadRequirements()
    }
  }, [expanded])

  const loadRequirements = async () => {
    try {
      setLoading(true)
      const data = await scheduleApi.getShiftRequirements(shift.id)
      const reqs = { ...requirements }
      data.forEach(r => {
        reqs[r.positionCode] = r.requiredCount
      })
      setRequirements(reqs)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בטעינת דרישות המשמרת')
    } finally {
      setLoading(false)
    }
  }

  const handleRequirementChange = async (pos: PositionCode, countStr: string) => {
    const count = parseInt(countStr) || 0
    if (count < 0) return

    setRequirements(prev => ({ ...prev, [pos]: count }))
    
    try {
      setError('')
      setSaving(true)
      await scheduleApi.setShiftRequirement(shift.id, {
        positionCode: pos,
        requiredCount: count,
      })
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בשמירת הדרישות')
      await loadRequirements()
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!confirm('האם אתה בטוח שברצונך למחוק משמרת זו? זה ימחק גם את הבקשות וההגשות הקשורות.')) return
    
    try {
      setSaving(true)
      await scheduleApi.deleteShift(shift.id)
      onDelete(shift.id)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה במחיקת המשמרת')
      setSaving(false)
    }
  }

  const d = new Date(shift.shiftDate)
  const dateStr = d.toLocaleDateString('he-IL', { weekday: 'long', day: 'numeric', month: 'long' })

  return (
    <div className="bg-white/5 border border-white/10 rounded-2xl overflow-hidden transition-all">
      <div 
        className="p-4 sm:p-6 flex flex-col sm:flex-row sm:items-center justify-between cursor-pointer hover:bg-white/5"
        onClick={() => setExpanded(!expanded)}
      >
        <div>
          <div className="flex items-center gap-3 mb-1">
            <h3 className="text-xl font-bold text-white">{shiftTypeLabels[shift.shiftType]}</h3>
            <span className="text-emerald-400 bg-emerald-400/10 px-2 py-0.5 rounded-md text-sm">
              {dateStr}
            </span>
          </div>
          <p className="text-slate-400 text-sm">לחץ כדי {expanded ? 'לסגור' : 'לערוך דרישות כוח אדם'}</p>
        </div>
        
        <div className="mt-4 sm:mt-0 flex gap-3">
          {isEditable && (
            <button
              onClick={(e) => { e.stopPropagation(); handleDelete() }}
              disabled={saving}
              className="px-4 py-2 bg-red-500/20 text-red-400 hover:bg-red-500/30 rounded-xl transition-colors text-sm font-semibold border border-red-500/30 disabled:opacity-50"
            >
              מחק משמרת
            </button>
          )}
        </div>
      </div>

      {expanded && (
        <div className="p-4 sm:p-6 border-t border-white/10 bg-black/20">
          {error && (
            <div className="bg-red-500/20 border border-red-500/50 text-red-200 p-3 rounded-lg mb-6 text-sm">
              {error}
            </div>
          )}

          {loading ? (
            <div className="text-slate-400 text-center py-4">טוען...</div>
          ) : (
            <div>
              <h4 className="text-white font-semibold mb-4">דרישות כוח אדם:</h4>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                {(Object.keys(requirements) as PositionCode[]).map(pos => (
                  <div key={pos} className="bg-slate-800/50 rounded-xl p-4 border border-white/5">
                    <label className="block text-slate-300 text-sm mb-2">{positionLabels[pos]}</label>
                    <input
                      type="number"
                      min="0"
                      disabled={!isEditable || saving}
                      value={requirements[pos]}
                      onChange={(e) => handleRequirementChange(pos, e.target.value)}
                      className="w-full bg-slate-900/50 border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-emerald-500 transition-colors"
                    />
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
