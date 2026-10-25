import { useEffect, useState } from "react";
import { adminService, type ActiveSession } from "../../services/admin-service";
import { Button } from "@/components/ui/button";

export const ActiveSessionsTab = () => {
  const [sessions, setSessions] = useState<ActiveSession[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchSessions = async () => {
    setLoading(true);
    try {
      const data = await adminService.GetActiveSessions();
      setSessions(data);
    } catch (err) {
      console.error("Failed to load sessions", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSessions();
  }, []);

  const handleForceLogout = async (sessionId: string) => {
    if (!window.confirm("Are you sure you want to force logout this session?")) return;
    try {
      await adminService.ForceLogout(sessionId);
      fetchSessions();
    } catch (err) {
      console.error("Failed to force logout", err);
    }
  };

  return (
    <div className="p-4 bg-white rounded shadow mt-4">
      <h3 className="text-lg font-semibold mb-4">Active Sessions</h3>
      {loading ? (
        <div>Loading...</div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Session ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">User ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Login At</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Last Activity</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {sessions.map((session) => (
                <tr key={session.session_id}>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{session.session_id}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{session.user_id}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(session.login_at).toLocaleString()}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(session.last_activity_at).toLocaleString()}</td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                      {session.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <Button 
                      variant="destructive" 
                      size="sm"
                      onClick={() => handleForceLogout(session.session_id)}
                    >
                      Force Logout
                    </Button>
                  </td>
                </tr>
              ))}
              {sessions.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-6 py-4 text-center text-sm text-gray-500">No active sessions found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
