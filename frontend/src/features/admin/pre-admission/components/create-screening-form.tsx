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

import { useCreateScreening } from "../hooks/use-create-screening";
import { usePendingResidents } from "../hooks/use-pending-residents";
import { useDialogStore } from "@/store/use-dialog-store";

const formSchema = z.object({
  residentId: z.string().min(1, "Please select a resident."),
});

export const CreateScreeningForm = () => {
  const { data: residents, isLoading } = usePendingResidents();
  const residentOptions = residents?.data ?? [];
  const createMutation = useCreateScreening();
  const close = useDialogStore((s) => s.close);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: { residentId: "" },
  });

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    createMutation.mutate(
      { residentId: Number(values.residentId) },
      { onSuccess: close },
    );
  };

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
      <FieldGroup>
        <Controller
          name="residentId"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor={field.name}>Resident</FieldLabel>
              <Select
                name={field.name}
                value={field.value}
                onValueChange={field.onChange}
              >
                <SelectTrigger
                  id={field.name}
                  aria-invalid={fieldState.invalid}
                >
                  <SelectValue
                    placeholder={isLoading ? "Loading..." : "Select resident"}
                  >
                    {
                      residentOptions.find((r) => String(r.id) === field.value)
                        ?.fullName
                    }
                  </SelectValue>
                </SelectTrigger>
                <SelectContent>
                  {residentOptions.map((r) => (
                    <SelectItem key={r.id} value={String(r.id)}>
                      {r.fullName}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
      </FieldGroup>
      <Button type="submit" disabled={createMutation.isPending}>
        Create
      </Button>
    </form>
  );
};
