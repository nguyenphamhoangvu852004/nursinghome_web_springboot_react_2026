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
import { Input } from "@/components/ui/input";

import { useCreateAdmission } from "../hooks/use-create-admission";
import { useCompletedScreenings } from "../../pre-admission/hooks/use-completed-screenings";
import { useDialogStore } from "@/store/use-dialog-store";
import { useFacilityForSelect } from "../hooks/use-facility-select";

const formSchema = z.object({
  preAdmissionScreeningId: z.string().min(1, "Please select a screening."),
  facilityId: z.string().min(1, "Please select a facility."),
  roomId: z.string().min(1, "Please select a room."),
  bedId: z.string().min(1, "Please select a bed."),
  admissionDate: z.string().min(1, "Required."),
});

export const CreateAdmissionForm = () => {
  const { data: screenings, isLoading } = useCompletedScreenings();
  const screeningOptions = screenings?.data ?? [];
  const { data: facilities, isLoading: isFacilityLoading } =
    useFacilityForSelect();
  const facilityOptions = facilities?.data ?? [];
  const createMutation = useCreateAdmission();
  const close = useDialogStore((s) => s.close);

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      preAdmissionScreeningId: "",
      facilityId: "",
      roomId: "",
      bedId: "",
      admissionDate: "",
    },
  });

  const selectedFacilityId = form.watch("facilityId");
  const selectedRoomId = form.watch("roomId");

  const selectedFacility = facilityOptions.find(
    (f) => String(f.id) === selectedFacilityId,
  );

  const roomOptions = selectedFacility?.rooms ?? [];

  const selectedRoom = roomOptions.find((r) => String(r.id) === selectedRoomId);

  const bedOptions = selectedRoom?.beds ?? [];

  const onSubmit = (values: z.infer<typeof formSchema>) => {
    createMutation.mutate(
      {
        preAdmissionScreeningId: Number(values.preAdmissionScreeningId),
        facilityId: Number(values.facilityId),
        bedId: Number(values.bedId),
        admissionDate: values.admissionDate,
      },
      {
        onSuccess: close,
      },
    );
  };

  return (
    <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
      <FieldGroup>
        <Controller
          name="preAdmissionScreeningId"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor={field.name}>
                Pre-Admission Screening
              </FieldLabel>
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
                    placeholder={isLoading ? "Loading..." : "Select screening"}
                  >
                    {
                      screeningOptions.find((s) => String(s.id) === field.value)
                        ?.residentName
                    }
                  </SelectValue>
                </SelectTrigger>
                <SelectContent>
                  {screeningOptions.map((s) => (
                    <SelectItem key={s.id} value={String(s.id)}>
                      #{s.id} - {s.residentName}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
        <Controller
          name="facilityId"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor={field.name}>Facility</FieldLabel>
              <Select
                value={field.value}
                onValueChange={(value) => {
                  field.onChange(value);

                  form.setValue("roomId", "");
                  form.setValue("bedId", "");
                }}
              >
                <SelectTrigger
                  id={field.name}
                  aria-invalid={fieldState.invalid}
                >
                  <SelectValue
                    placeholder={
                      isFacilityLoading ? "Loading..." : "Select facility"
                    }
                  >
                    {
                      facilityOptions.find((f) => String(f.id) === field.value)
                        ?.name
                    }
                  </SelectValue>
                </SelectTrigger>
                <SelectContent>
                  {facilityOptions.map((f) => (
                    <SelectItem key={f.id} value={String(f.id)}>
                      {f.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
        <Controller
          name="roomId"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel>Room</FieldLabel>

              <Select
                value={field.value}
                onValueChange={(value) => {
                  field.onChange(value);
                  form.setValue("bedId", "");
                }}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select room">
                    {
                      roomOptions.find((r) => String(r.id) === field.value)
                        ?.roomNumber
                    }
                  </SelectValue>
                </SelectTrigger>

                <SelectContent>
                  {roomOptions.map((room) => (
                    <SelectItem key={room.id} value={String(room.id)}>
                      {room.roomNumber}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
        <Controller
          name="bedId"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel>Bed</FieldLabel>

              <Select value={field.value} onValueChange={field.onChange}>
                <SelectTrigger>
                  <SelectValue placeholder="Select bed">
                    {
                      bedOptions.find((b) => String(b.id) === field.value)
                        ?.bedNumber
                    }
                  </SelectValue>
                </SelectTrigger>

                <SelectContent>
                  {bedOptions.map((bed) => (
                    <SelectItem key={bed.id} value={String(bed.id)}>
                      {bed.bedNumber}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
            </Field>
          )}
        />
        <Controller
          name="admissionDate"
          control={form.control}
          render={({ field, fieldState }) => (
            <Field data-invalid={fieldState.invalid}>
              <FieldLabel htmlFor={field.name}>Admission Date</FieldLabel>
              <Input
                {...field}
                id={field.name}
                type="date"
                aria-invalid={fieldState.invalid}
              />
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
