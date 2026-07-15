import { useState, useEffect, type FormEvent } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { api } from '../../api/client'
import { useAuth } from '../../context/AuthContext'
import type { RequestCodeRequest, VerifyCodeRequest } from '../../api/types'

export default function LoginPage() {
  const navigate = useNavigate()
  const [phone, setPhone] = useState('')
  const [code, setCode] = useState('')
  const [step, setStep] = useState<1 | 2>(1)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [codeSentMessage, setCodeSentMessage] = useState('')
  const { user, checkAuth } = useAuth()

  useEffect(() => {
    if (user) {
      navigate('/home')
    }
  }, [user, navigate])

  const handleRequestCode = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const body: RequestCodeRequest = { phone }
      await api.post<{ message: string }>('/auth/request-code', body)
      setStep(2)
      setCodeSentMessage('קוד אימות נשלח לטלפון שלך')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה לא צפויה')
    } finally {
      setLoading(false)
    }
  }

  const handleVerifyCode = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const body: VerifyCodeRequest = { phone, code }
      await api.post('/auth/verify-code', body)
      await checkAuth()
      navigate('/home')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה לא צפויה')
    } finally {
      setLoading(false)
    }
  }

  const handleResendCode = async () => {
    setError('')
    setCode('')
    setLoading(true)

    try {
      const body: RequestCodeRequest = { phone }
      await api.post<{ message: string }>('/auth/request-code', body)
      setCodeSentMessage('קוד חדש נשלח')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'שגיאה לא צפויה')
    } finally {
      setLoading(false)
    }
  }

  const handleBackToPhone = () => {
    setStep(1)
    setCode('')
    setError('')
    setCodeSentMessage('')
  }

  return (
    <div className="min-h-screen bg-gradient-to-bl from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-white/10 backdrop-blur-xl border border-white/20 rounded-2xl p-8 shadow-2xl">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-white mb-2">Lyon B'Touch</h1>
          <p className="text-slate-400">
            {step === 1 ? 'התחברות למערכת' : 'הזנת קוד אימות'}
          </p>
        </div>

        {step === 1 ? (
          <form onSubmit={handleRequestCode} className="space-y-5">
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
              {loading ? 'שולח...' : 'שלח קוד אימות'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleVerifyCode} className="space-y-5">
            {codeSentMessage && (
              <div className="p-3 bg-emerald-500/20 border border-emerald-500/30 rounded-xl text-emerald-300 text-sm text-center">
                {codeSentMessage}
              </div>
            )}

            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1.5">
                קוד אימות (6 ספרות)
              </label>
              <input
                type="text"
                value={code}
                onChange={(e) => setCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                required
                dir="ltr"
                maxLength={6}
                className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500/50 transition-all text-center text-2xl tracking-[0.5em] font-mono"
                placeholder="000000"
                autoFocus
              />
            </div>

            {error && (
              <div className="p-3 bg-red-500/20 border border-red-500/30 rounded-xl text-red-300 text-sm text-center">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading || code.length !== 6}
              className="w-full py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 disabled:opacity-50 disabled:cursor-not-allowed text-white font-semibold transition-colors cursor-pointer"
            >
              {loading ? 'מאמת...' : 'אימות קוד'}
            </button>

            <div className="flex justify-between text-sm">
              <button
                type="button"
                onClick={handleResendCode}
                disabled={loading}
                className="text-emerald-400 hover:text-emerald-300 transition-colors cursor-pointer disabled:opacity-50"
              >
                שלח קוד חדש
              </button>
              <button
                type="button"
                onClick={handleBackToPhone}
                className="text-slate-400 hover:text-slate-300 transition-colors cursor-pointer"
              >
                שינוי מספר טלפון
              </button>
            </div>
          </form>
        )}

        <p className="text-center text-slate-400 text-sm mt-6">
          אין לך חשבון?{' '}
          <Link to="/register" className="text-emerald-400 hover:text-emerald-300 transition-colors">
            הרשמה
          </Link>
        </p>
      </div>
    </div>
  )
}
