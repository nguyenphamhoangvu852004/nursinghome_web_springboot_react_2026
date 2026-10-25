import { zodResolver } from "@hookform/resolvers/zod";
import { Controller, useForm } from "react-hook-form";
import z from "zod";

import { Button } from "@/components/ui/button";
import {
  Field,
  FieldError,
  FieldGroup,
  FieldLabel,
} from "@/components/ui/field";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";

import { useDecideAssessment } from "../hooks/use-decide-assessment";
import { useCareLevels } from "../hooks/use-care-levels";
import { useDialogStore } from "@/store/use-dialog-store";
import type { AssessmentResponse } from "../types/assessment-type";

const formSchema = z
  .object({
    status: z.enum(["COMPLETED", "REJECTED"]),
    confirmedCareLevelId: z.number().min(1, "Please select a care level."),
    overrideReason: z.string().optional(),
  })
  .refine(
    (data) =>
      data.confirmedCareLevelId === undefined || !data.overrideReason || true,
    {},
  );

export const DecideAssessmentForm = ({ row }: { row: AssessmentResponse }) => {
  const { data: careLevels, isLoading } = useCareLevels();
  const careLevelOptions = Array.isArray(careLevels) ? careLevels : [];
  const decideMutation = useDecideAssessment();
  const close = useDialogStore((s) => s.close);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      status: "COMPLETED",
      confirmedCareLevelId: row.suggestedCareLevelId,
      overrideReason: "",
    },
  });

  const confirmedCareLevelId = form.watch("confirmedCareLevelId");
  const isOverridden = confirmedCareLevelId !== row.suggestedCareLevelId;

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    if (isOverridden && !values.overrideReason?.trim()) {
      form.setError("overrideReason", {
        message: "Override reason is required.",
      });
      return;
    }

    decideMutation.mutate(
      {
        id: row.id,
        payload: {
          status: values.status,
          confirmedCareLevelId: Number(values.confirmedCareLevelId),
          overrideReason: isOverridden ? values.overrideReason : undefined,
        },
      },
      { onSuccess: close },
    );
  };

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
      <FieldGroup>
        <Controller
          name="status"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor={field.name}>Decision</FieldLabel>
              <Select
                name={field.name}
                value={field.value}
                onValueChange={field.onChange}
              >
                <SelectTrigger
                  id={field.name}
                  aria-invalid={fieldState.invalid}
                >
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="COMPLETED">Complete</SelectItem>
                  <SelectItem value="REJECTED">Reject</SelectItem>
                </SelectContent>
              </Select>
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
        <Controller
          name="confirmedCareLevelId"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor={field.name}>Confirmed Care Level</FieldLabel>
              <Select
                name={field.name}
                value={field.value}
                onValueChange={(value) => field.onChange(Number(value))}
              >
                <SelectTrigger
                  id={field.name}
                  aria-invalid={fieldState.invalid}
                >
                  <SelectValue
                    placeholder={isLoading ? "Loading..." : "Select care level"}
                  >
                    {
                      careLevels?.find((c) => c.id === Number(field.value))
                        ?.levelName
                    }
                  </SelectValue>
                </SelectTrigger>
                <SelectContent>
                  {careLevelOptions.map((c) => (
                    <SelectItem key={c.id} value={String(c.id)}>
                      {c.levelName}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
        {isOverridden && (
          <Controller
            name="overrideReason"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid}>
                <FieldLabel htmlFor={field.name}>Override Reason</FieldLabel>
                <Textarea
                  id={field.name}
                  value={field.value}
                  onChange={field.onChange}
                  aria-invalid={fieldState.invalid}
                />
                {fieldState.invalid && (
                  <FieldError errors={[fieldState.error]} />
                )}
              </Field>
            )}
          />
        )}
      </FieldGroup>
      <Button type="submit" disabled={decideMutation.isPending}>
        Confirm
      </Button>
    </form>
  );
};
