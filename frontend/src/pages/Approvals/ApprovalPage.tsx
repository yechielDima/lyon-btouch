import { useState, useEffect } from 'react'
import { api } from '../../api/client'
import { SystemRole, PositionCode } from '../../api/types'
import type { UserResponse, ApproveUserRequest } from '../../api/types'

interface ApprovalFormState {
  systemRole: SystemRole
  qualifications: Set<string>
  isChecker: boolean
  loading: boolean
  error: string
}

export default function ApprovalPage() {
  const [pendingUsers, setPendingUsers] = useState<UserResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [forms, setForms] = useState<Record<number, ApprovalFormState>>({})

  const fetchPendingUsers = async () => {
    try {
      setLoading(true)
      const users = await api.get<UserResponse[]>('/users/pending')
      setPendingUsers(users)
      
      const initialForms: Record<number, ApprovalFormState> = {}
      users.forEach(u => {
        initialForms[u.id] = {
          systemRole: SystemRole.EMPLOYEE,
          qualifications: new Set<string>(),
          isChecker: false,
          loading: false,
          error: ''
        }
      })
      setForms(initialForms)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה בטעינת משתמשים')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPendingUsers()
  }, [])

  const handleRoleChange = (userId: number, role: SystemRole) => {
    setForms(prev => {
      const state = { ...prev[userId], systemRole: role }
      if (role === SystemRole.SHIFT_MANAGER) {
        state.isChecker = true
        state.qualifications = new Set(state.qualifications).add(PositionCode.WAITER)
      }
      return { ...prev, [userId]: state }
    })
  }

  const handleCheckerChange = (userId: number, checked: boolean) => {
    setForms(prev => {
      const state = { ...prev[userId] }
      if (state.systemRole === SystemRole.SHIFT_MANAGER) {
        return prev
      }
      state.isChecker = checked
      if (checked) {
        state.qualifications = new Set(state.qualifications).add(PositionCode.WAITER)
      }
      return { ...prev, [userId]: state }
    })
  }

  const toggleQualification = (userId: number, qual: string) => {
    setForms(prev => {
      const state = { ...prev[userId] }
      
      if (qual === PositionCode.WAITER && (state.systemRole === SystemRole.SHIFT_MANAGER || state.isChecker)) {
        return prev
      }

      const newQuals = new Set(state.qualifications)
      if (newQuals.has(qual)) {
        newQuals.delete(qual)
      } else {
        newQuals.add(qual)
      }
      state.qualifications = newQuals
      return { ...prev, [userId]: state }
    })
  }

  const handleApprove = async (userId: number) => {
    const form = forms[userId]
    setForms(prev => ({ ...prev, [userId]: { ...form, loading: true, error: '' } }))

    try {
      const body: ApproveUserRequest = {
        systemRole: form.systemRole,
        qualifications: Array.from(form.qualifications),
        isChecker: form.isChecker
      }
      await api.put(`/users/${userId}/approve`, body)
      setPendingUsers(prev => prev.filter(u => u.id !== userId))
    } catch (err) {
      setForms(prev => ({
        ...prev,
        [userId]: { ...form, loading: false, error: err instanceof Error ? err.message : 'שגיאה באישור' }
      }))
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4" dir="rtl">
        <div className="text-emerald-500 text-xl">טוען...</div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-bl from-slate-900 via-slate-800 to-slate-900 p-4 md:p-8" dir="rtl">
      <div className="max-w-4xl mx-auto">
        <h1 className="text-3xl font-bold text-white mb-8">אישור עובדים</h1>
        
        {error && (
          <div className="bg-red-500/20 border border-red-500/50 text-red-200 p-4 rounded-xl mb-6">
            {error}
          </div>
        )}

        {pendingUsers.length === 0 ? (
          <div className="bg-white/5 border border-white/10 rounded-2xl p-12 text-center">
            <div className="text-6xl mb-4">✨</div>
            <h2 className="text-2xl text-white font-semibold">אין בקשות ממתינות</h2>
            <p className="text-slate-400 mt-2">כל העובדים אושרו בהצלחה.</p>
          </div>
        ) : (
          <div className="grid gap-6">
            {pendingUsers.map(user => {
              const form = forms[user.id]
              if (!form) return null

              return (
                <div key={user.id} className="bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-6">
                  <div className="flex justify-between items-start mb-6 border-b border-white/10 pb-4">
                    <div>
                      <h2 className="text-xl font-bold text-white">{user.fullName}</h2>
                      <p className="text-slate-400">{user.phone}</p>
                    </div>
                  </div>

                  {form.error && (
                    <div className="bg-red-500/20 border border-red-500/50 text-red-200 p-3 rounded-lg mb-4 text-sm">
                      {form.error}
                    </div>
                  )}

                  <div className="space-y-6">
                    <div>
                      <label className="block text-slate-300 mb-2 font-semibold">תפקיד מערכת</label>
                      <select
                        value={form.systemRole}
                        onChange={(e) => handleRoleChange(user.id, e.target.value as SystemRole)}
                        className="w-full bg-slate-800/50 border border-white/10 rounded-xl px-4 py-2.5 text-white focus:outline-none focus:border-emerald-500 transition-colors"
                      >
                        <option value={SystemRole.EMPLOYEE}>עובד</option>
                        <option value={SystemRole.SHIFT_MANAGER}>אחמ"ש</option>
                        <option value={SystemRole.MANAGER}>מנהל</option>
                      </select>
                    </div>

                    <div>
                      <label className="block text-slate-300 mb-2 font-semibold">הסמכות (ניתן לבחור מרובים)</label>
                      <div className="flex flex-wrap gap-2">
                        {[
                          { value: PositionCode.WAITER, label: 'מלצר' },
                          { value: PositionCode.BARTENDER, label: 'ברמן' },
                          { value: PositionCode.COOK, label: 'טבח' },
                          { value: PositionCode.HOST, label: 'מארחת' }
                        ].map(pos => {
                          const isWaiterLocked = pos.value === PositionCode.WAITER && 
                                               (form.systemRole === SystemRole.SHIFT_MANAGER || form.isChecker);
                          return (
                            <button
                              key={pos.value}
                              onClick={() => toggleQualification(user.id, pos.value)}
                              disabled={isWaiterLocked}
                              className={`px-4 py-2 rounded-xl transition-colors border ${
                                form.qualifications.has(pos.value)
                                  ? 'bg-emerald-500/20 border-emerald-500/50 text-emerald-400'
                                  : 'bg-slate-800/50 border-white/10 text-slate-400 hover:bg-slate-700/50'
                              } ${isWaiterLocked ? 'opacity-50 cursor-not-allowed' : ''}`}
                            >
                              {pos.label}
                            </button>
                          )
                        })}
                      </div>
                    </div>

                    <div>
                      <label className="flex items-center space-x-3 space-x-reverse cursor-pointer">
                        <input
                          type="checkbox"
                          checked={form.isChecker}
                          onChange={(e) => handleCheckerChange(user.id, e.target.checked)}
                          disabled={form.systemRole === SystemRole.SHIFT_MANAGER}
                          className="w-5 h-5 rounded border-white/10 bg-slate-800/50 text-emerald-500 focus:ring-emerald-500/50 focus:ring-offset-slate-900"
                        />
                        <span className={`text-slate-300 font-semibold ${form.systemRole === SystemRole.SHIFT_MANAGER ? 'opacity-50' : ''}`}>
                          צ'קר (מושך אוטומטית מלצרות)
                        </span>
                      </label>
                    </div>

                    <div className="pt-4 mt-4 border-t border-white/10 flex justify-end">
                      <button
                        onClick={() => handleApprove(user.id)}
                        disabled={form.loading}
                        className="bg-emerald-500 hover:bg-emerald-600 text-white font-semibold py-2.5 px-8 rounded-xl transition-colors disabled:opacity-50"
                      >
                        {form.loading ? 'מאשר...' : 'אשר'}
                      </button>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}
