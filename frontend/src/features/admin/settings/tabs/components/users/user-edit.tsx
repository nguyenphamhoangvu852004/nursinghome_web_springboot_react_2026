interface UserEditProps {
  user: any;
  onBack: () => void;
}

export function UserEdit({
  user,
  onBack,
}: UserEditProps) {
  return (
    <div className="space-y-6">

      <div>
        <p className="text-sm text-gray-400">
          Admin &gt; Users &gt; {user ? "Edit User" : "Add User"}
        </p>

        <h1 className="text-3xl font-bold">
          {user ? "Edit User" : "Add User"}
        </h1>

        <p className="text-gray-500">
          New accounts start as Invited.
        </p>
      </div>

      <div className="grid grid-cols-3 gap-6">

        {/* LEFT */}

        <div className="col-span-2 rounded-xl border bg-white p-6">

          <h2 className="mb-6 text-xl font-semibold">
            Account Details
          </h2>

          <div className="grid grid-cols-2 gap-4">

            <div>
              <label className="mb-2 block text-sm font-medium">
                Full Name
              </label>

              <input
                defaultValue={user?.fullName}
                className="w-full rounded-lg border p-3"
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium">
                Email
              </label>

              <input
                defaultValue={user?.email}
                className="w-full rounded-lg border p-3"
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium">
                Phone
              </label>

              <input
                defaultValue={user?.phone}
                className="w-full rounded-lg border p-3"
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium">
                Role
              </label>

              <select className="w-full rounded-lg border p-3">

                <option>Admission</option>

                <option>DON</option>

                <option>Nurse</option>

                <option>CNA</option>

                <option>Billing</option>

                <option>System Admin</option>

              </select>

            </div>

            <div>
              <label className="mb-2 block text-sm font-medium">
                Status
              </label>

              <input
                defaultValue={user?.status ?? "Invited"}
                disabled
                className="w-full rounded-lg border bg-gray-100 p-3"
              />
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium">
                Assigned Facility
              </label>

              <select className="w-full rounded-lg border p-3">
                <option>None</option>
              </select>
            </div>

          </div>

        </div>

        {/* RIGHT */}

        <div className="rounded-xl border bg-white p-6">

          <h2 className="mb-4 text-lg font-semibold">
            What happens next
          </h2>

          <ol className="space-y-3 text-sm text-gray-600">

            <li>1. User receives an invitation email.</li>

            <li>2. User sets password.</li>

            <li>3. User confirms phone.</li>

            <li>4. 2FA becomes Enabled.</li>

          </ol>

          <div className="mt-6 rounded-lg border border-blue-300 bg-blue-50 p-4 text-sm text-blue-700">
            Login accepts whichever identifier
            was registered: Email OR Phone.
          </div>

        </div>

      </div>

      {/* FOOTER */}

      <div className="flex justify-end gap-4">

        <button
          onClick={onBack}
          className="rounded-lg border px-8 py-3"
        >
          Cancel
        </button>

        <button
          className="rounded-lg bg-blue-600 px-8 py-3 text-white hover:bg-blue-700"
        >
          {user ? "Save Changes" : "Create User"}
        </button>

      </div>

    </div>
  );
}