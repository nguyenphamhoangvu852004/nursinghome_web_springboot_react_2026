import { useState } from "react";

import { UserList } from "./components/users/user-list";
import { UserEdit } from "./components/users/user-edit";

export const UserTab = () => {

    const [mode, setMode] =
        useState<"list" | "edit" | "add">("list");

    const [selectedUser, setSelectedUser] =
        useState<any>(null);

    if (mode === "edit") {

        return (

            <UserEdit

                user={selectedUser}

                onBack={() => setMode("list")}

            />

        );

    }

    if (mode === "add") {

        return (

            <UserEdit

                user={null}

                onBack={() => setMode("list")}

            />

        );

    }

    return (

        <UserList

            onAdd={() => {

                setMode("add");

            }}

            onEdit={(user) => {

                setSelectedUser(user);

                setMode("edit");

            }}

        />

    );

};