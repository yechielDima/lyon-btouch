import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/Login/LoginPage'
import RegisterPage from './pages/Register/RegisterPage'
import HomePage from './pages/Home/HomePage'
import ApprovalPage from './pages/Approvals/ApprovalPage'
import ScheduleBuilderPage from './pages/Schedule/ScheduleBuilderPage'
import AvailabilityPage from './pages/Availability/AvailabilityPage'
import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import { SystemRole } from './api/types'

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          
          <Route element={<ProtectedRoute />}>
            <Route path="/home" element={<HomePage />} />
            <Route path="/availability" element={<AvailabilityPage />} />
          </Route>

          <Route element={<ProtectedRoute allowedRoles={[SystemRole.MANAGER]} />}>
            <Route path="/approvals" element={<ApprovalPage />} />
            <Route path="/schedule" element={<ScheduleBuilderPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}

export default App
