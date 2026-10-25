import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import type { EquipmentFormValues } from "../../store/use-equipment-store";

interface SelectOption {
  id: number;
  name: string;
}

interface EquipmentFormViewProps {
  mode: "create" | "update";
  title: string;
  actionLabel: string;
  cancelLabel: string;
  initialValues: EquipmentFormValues;
  categories: SelectOption[];
  facilities: SelectOption[];
  isSubmitting: boolean;
  onCancel: () => void;
  onSubmit: (values: EquipmentFormValues) => Promise<void> | void;
}

const buildSchema = (mode: "create" | "update"): z.ZodType<EquipmentFormValues> =>
  z.object({
    itemName: z.string().min(1, "Equipment name is required"),
    categoryId: z.string().min(1, "Category is required"),
    assetTag: z.string(),
    facilityId: z.string().min(1, "Facility is required"),
    unitValue: z.string().min(1, "Unit value is required"),
  }).superRefine((values, context) => {
    if (mode === "create" && !values.assetTag?.trim()) {
      context.addIssue({
        code: z.ZodIssueCode.custom,
        message: "Asset tag is required",
        path: ["assetTag"],
      });
    }
  });

export function EquipmentFormView({
  mode,
  title,
  actionLabel,
  cancelLabel,
  initialValues,
  categories,
  facilities,
  isSubmitting,
  onCancel,
  onSubmit,
}: EquipmentFormViewProps) {
  const schema = buildSchema(mode);

  const form = useForm<EquipmentFormValues>({
    resolver: zodResolver(schema as never) as never,
    defaultValues: initialValues,
  });

  useEffect(() => {
    form.reset(initialValues);
  }, [form, initialValues]);

  return (
    <Form {...form}>
      <form
        className="space-y-5 rounded-2xl border border-slate-200 bg-white p-4 shadow-[0_1px_0_rgba(15,23,42,0.03)] sm:p-6"
        onSubmit={form.handleSubmit(onSubmit)}
      >
        <h3 className="text-lg font-semibold text-slate-900">{title}</h3>

        <div className="grid gap-5 rounded-xl border border-slate-100 bg-slate-50/40 p-4 sm:p-6 lg:grid-cols-2">
          <FormField
            control={form.control}
            name="itemName"
            render={({ field }) => (
              <FormItem className="space-y-2">
                <FormLabel className="text-sm font-semibold text-slate-800">Equipment name</FormLabel>
                <FormControl>
                  <Input {...field} className="h-11 rounded-sm border-slate-300 bg-white text-slate-900" placeholder="Equipment name" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="assetTag"
            render={({ field }) => (
              <FormItem className="space-y-2">
                <FormLabel className="text-sm font-semibold text-slate-800">Asset tag</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    className="h-11 rounded-sm border-slate-300 bg-white text-slate-900"
                    disabled={mode === "update"}
                    placeholder="Asset tag"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="categoryId"
            render={({ field }) => (
              <FormItem className="space-y-2">
                <FormLabel className="text-sm font-semibold text-slate-800">Category</FormLabel>
                <FormControl>
                  <select
                    {...field}
                    className="h-11 w-full rounded-sm border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none focus:border-slate-400"
                  >
                    <option value="">Select category</option>
                    {categories.map((category) => (
                      <option key={category.id} value={String(category.id)}>
                        {category.name}
                      </option>
                    ))}
                  </select>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="facilityId"
            render={({ field }) => (
              <FormItem className="space-y-2">
                <FormLabel className="text-sm font-semibold text-slate-800">Facility</FormLabel>
                <FormControl>
                  <select
                    {...field}
                    className="h-11 w-full rounded-sm border border-slate-300 bg-white px-3 text-sm text-slate-900 outline-none focus:border-slate-400"
                  >
                    <option value="">Select facility</option>
                    {facilities.map((facility) => (
                      <option key={facility.id} value={String(facility.id)}>
                        {facility.name}
                      </option>
                    ))}
                  </select>
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="unitValue"
            render={({ field }) => (
              <FormItem className="space-y-2 lg:col-span-2">
                <FormLabel className="text-sm font-semibold text-slate-800">Unit value</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    className="h-11 max-w-[240px] rounded-sm border-slate-300 bg-white text-slate-900"
                    min="0"
                    placeholder="0.00"
                    step="0.01"
                    type="number"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <div className="flex flex-col-reverse gap-3 pt-2 sm:flex-row sm:justify-end">
          <Button
            className="h-11 min-w-32 rounded-md bg-blue-600 px-6 text-base font-semibold text-white shadow-sm hover:bg-blue-700"
            disabled={isSubmitting}
            type="submit"
          >
            {isSubmitting ? "Saving..." : actionLabel}
          </Button>
          <Button
            className="h-11 min-w-32 rounded-md border border-slate-300 bg-white px-6 text-base font-semibold text-slate-600 shadow-none hover:bg-slate-50"
            disabled={isSubmitting}
            onClick={onCancel}
            type="button"
            variant="outline"
          >
            {cancelLabel}
          </Button>
        </div>
      </form>
    </Form>
  );
}