import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { SystemRole } from '../api/types'

interface ProtectedRouteProps {
  allowedRoles?: SystemRole[]
}

export default function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
  const { user, loading } = useAuth()

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
        <div className="text-emerald-500 text-xl">טוען...</div>
      </div>
    )
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (allowedRoles && !allowedRoles.includes(user.systemRole)) {
    return (
      <div className="min-h-screen bg-slate-900 flex flex-col items-center justify-center p-4">
        <h1 className="text-3xl font-bold text-red-500 mb-4">403 - גישה נדחתה</h1>
        <p className="text-slate-300">אין לך הרשאות לצפות בעמוד זה.</p>
      </div>
    )
  }

  return <Outlet />
}
