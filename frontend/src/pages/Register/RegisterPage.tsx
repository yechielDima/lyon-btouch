import { useState, type FormEvent } from 'react'
import { api } from '../../api/client'
import type { RegisterRequest, UserResponse } from '../../api/types'
import { Link } from 'react-router-dom'

export default function RegisterPage() {
  const [fullName, setFullName] = useState('')
  const [phone, setPhone] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const body: RegisterRequest = { fullName, phone }
      await api.post<UserResponse>('/auth/register', body)
      setSuccess(true)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה לא צפויה')
    } finally {
      setLoading(false)
    }
  }

  if (success) {
    return (
      <div className="min-h-screen bg-gradient-to-bl from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4">
        <div className="w-full max-w-md bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-8 text-center shadow-2xl">
          <div className="text-6xl mb-4">✅</div>
          <h2 className="text-2xl font-bold text-white mb-3">הרשמה נשלחה בהצלחה</h2>
          <p className="text-slate-300 mb-6">
            החשבון שלך ממתין לאישור מנהל. תקבל/י הודעה ברגע שהחשבון יאושר.
          </p>
          <Link
            to="/login"
            className="inline-block w-full py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-white font-semibold transition-colors"
          >
            חזרה להתחברות
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gradient-to-bl from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-8 shadow-2xl">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-white mb-2">Lyon B'Touch</h1>
          <p className="text-slate-400">הרשמה למערכת</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1.5">שם מלא</label>
            <input
              type="text"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
              className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all"
              placeholder="הכנס/י שם מלא"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1.5">טלפון</label>
            <input
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              required
              dir="ltr"
              className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all text-left"
              placeholder="050-1234567"
            />
          </div>

          {error && (
            <div className="p-3 bg-red-500/20 border border-red-500/30 rounded-xl text-red-300 text-sm text-center">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold transition-colors cursor-pointer"
          >
            {loading ? 'שולח...' : 'הרשמה'}
          </button>
        </form>

        <p className="text-center text-slate-400 text-sm mt-6">
          כבר יש לך חשבון?{' '}
          <Link to="/login" className="text-emerald-400 hover:text-emerald-300 transition-colors">
            התחברות
          </Link>
        </p>
      </div>
    </div>
  )
}
