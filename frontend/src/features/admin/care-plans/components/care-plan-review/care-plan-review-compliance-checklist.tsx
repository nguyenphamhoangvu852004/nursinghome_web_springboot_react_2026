type ChecklistItem = {
  id: string;
  label: string;
  checked: boolean;
};

import { useState } from "react";
import Text from "../../ui/Text";
import Title from "../../ui/Title";
import Flag from "../../ui/flag";

export default function CarePlanReviewComplianceChecklist() {
  const [items, setItems] = useState<ChecklistItem[]>([
    {
      id: "plan-started",
      label: "Plan started within 48h",
      checked: true,
    },
    {
      id: "comprehensive-plan",
      label: "Comprehensive plan within 7 days",
      checked: true,
    },
    {
      id: "mds-linked",
      label: "MDS assessment linked",
      checked: false,
    },
    {
      id: "title22",
      label: "CA Title 22 items addressed",
      checked: false,
    },
  ]);

  const handleToggle = (id: string) => {
    setItems((prev) =>
      prev.map((item) =>
        item.id === id ? { ...item, checked: !item.checked } : item,
      ),
    );
  };

  const completed = items.filter((item) => item.checked).length;

  return (
    <div className="bg-[#fafcfe] border-2 border-gray-200 rounded-lg p-3">
      <Title className="text-[16px] mb-2">Compliance Checklist</Title>

      <Text>All items required to approve.</Text>

      <div className="mt-2 space-y-2">
        {items.map((item) => (
          <label
            key={item.id}
            className="flex items-center gap-3 cursor-pointer"
          >
            <input
              type="checkbox"
              checked={item.checked}
              onChange={() => handleToggle(item.id)}
            />

            <Text>{item.label}</Text>
          </label>
        ))}
      </div>

      <Flag className="mt-3" title={`${completed}/${items.length} Completed`} />
    </div>
  );
}
