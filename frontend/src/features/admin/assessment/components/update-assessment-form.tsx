import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, useForm } from "react-hook-form";
import z from "zod";

import { Button } from "@/components/ui/button";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
  FieldLegend,
  FieldSet,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";

import { useUpdateAssessment } from "../hooks/use-update-assessment";
import { useDialogStore } from "@/store/use-dialog-store";
import type { AssessmentResponse } from "../types/assessment-type";

const formSchema = z.object({
  details: z.array(
    z.object({
      metricId: z.number(),
      score: z.number().min(0, "Score must be >= 0."),
      notes: z.string().optional(),
    }),
  ),
});

export const UpdateAssessmentForm = ({ row }: { row: AssessmentResponse }) => {
  const updateMutation = useUpdateAssessment(row.id);
  const close = useDialogStore((s) => s.close);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      details: row.details.map((d) => ({
        metricId: d.metricId,
        score: d.score,
        notes: d.notes ?? "",
      })),
    },
  });

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    updateMutation.mutate(values, { onSuccess: close });
  };

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
      <FieldGroup>
        <FieldSet>
          <FieldLegend variant="label">Metric Scores</FieldLegend>
          <FieldGroup className="gap-3">
            {row.details.map((d, index) => (
              <Controller
                key={d.metricId}
                name={`details.${index}.score`}
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field
                    orientation="horizontal"
                    data-invalid={fieldState.invalid}
                  >
                    <FieldLabel htmlFor={field.name} className="flex-1">
                      {d.metricName}
                      <span className="text-muted-foreground ml-1 text-xs">
                        ({d.category})
                      </span>
                    </FieldLabel>
                    <Input
                      {...field}
                      id={field.name}
                      type="number"
                      className="w-24"
                      aria-invalid={fieldState.invalid}
                      onChange={(e) => field.onChange(Number(e.target.value))}
                    />
                    {fieldState.invalid && (
                      <FieldError errors={[fieldState.error]} />
                    )}
                  </Field>
                )}
              />
            ))}
          </FieldGroup>
        </FieldSet>
      </FieldGroup>
      <Button type="submit" disabled={updateMutation.isPending}>
        Update
      </Button>
    </form>
  );
};
