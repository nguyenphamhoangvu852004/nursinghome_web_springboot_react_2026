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
import { Textarea } from "@/components/ui/textarea";

const categorySchema = z.object({
  categoryName: z.string().min(1, "Category name is required"),
  description: z.string().min(1, "Description is required"),
});

type CategoryFormValues = z.infer<typeof categorySchema>;

interface CategoryFormViewProps {
  title: string;
  actionLabel: string;
  cancelLabel: string;
  initialValues: CategoryFormValues;
  isSubmitting: boolean;
  onCancel: () => void;
  onSubmit: (values: CategoryFormValues) => Promise<void> | void;
}

export function CategoryFormView({
  title,
  actionLabel,
  cancelLabel,
  initialValues,
  isSubmitting,
  onCancel,
  onSubmit,
}: CategoryFormViewProps) {
  const form = useForm<CategoryFormValues>({
    resolver: zodResolver(categorySchema),
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

        <div className="space-y-5 rounded-xl border border-slate-100 bg-slate-50/40 p-4 sm:p-6">
          <FormField
            control={form.control}
            name="categoryName"
            render={({ field }) => (
              <FormItem className="space-y-2">
                <FormLabel className="text-sm font-semibold text-slate-800">Category name</FormLabel>
                <FormControl>
                  <Input
                    {...field}
                    className="h-11 max-w-[240px] rounded-sm border-slate-300 bg-white text-slate-900"
                    placeholder="Category name"
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="description"
            render={({ field }) => (
              <FormItem className="space-y-2">
                <FormLabel className="text-sm font-semibold text-slate-800">Description</FormLabel>
                <FormControl>
                  <Textarea
                    {...field}
                    className="min-h-24 rounded-sm border-slate-300 bg-white text-slate-900 sm:min-h-28"
                    placeholder="Description"
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
