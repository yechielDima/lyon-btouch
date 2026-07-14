import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import type { User } from '../../api/types'

export default function HomePage() {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)

  useEffect(() => {
    const stored = localStorage.getItem('currentUser')
    if (!stored) {
      navigate('/login')
      return
    }
    try {
      setUser(JSON.parse(stored))
    } catch {
      navigate('/login')
    }
  }, [navigate])

  const handleLogout = () => {
    localStorage.removeItem('currentUser')
    navigate('/login')
  }

  if (!user) return null

  return (
    <div className="min-h-screen bg-gradient-to-bl from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-8 text-center shadow-2xl">
        <div className="text-6xl mb-4">👋</div>
        <h1 className="text-2xl font-bold text-white mb-2">
          שלום, {user.fullName}!
        </h1>
        <p className="text-slate-400 mb-1">
          תפקיד: {user.systemRole === 'MANAGER' ? 'מנהל' : user.systemRole === 'SHIFT_MANAGER' ? 'מנהל משמרת' : 'עובד'}
        </p>
        <p className="text-slate-500 text-sm mb-8">
          ניקוד אמינות: {user.reliabilityScore}
        </p>
        <button
          onClick={handleLogout}
          className="w-full py-3 rounded-xl bg-red-500/80 hover:bg-red-500 text-white font-semibold transition-colors cursor-pointer"
        >
          התנתקות
        </button>
      </div>
    </div>
  )
}
