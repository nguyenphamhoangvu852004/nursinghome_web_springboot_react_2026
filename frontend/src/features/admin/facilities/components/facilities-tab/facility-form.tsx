import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
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
import type { Facility } from "@/services/facilities-api";

const facilitySchema = z.object({
  name: z.string().min(1, "Facility name is required"),
  facility_code: z.string().min(1, "Code is required"),
  license_number: z.string().min(1, "License number is required"),
  target_state: z.string().length(2, "State must be exactly 2 characters (e.g., CA)"),
  phone_number: z.string().min(1, "Phone is required"),
});

type FacilityFormValues = z.infer<typeof facilitySchema>;

interface FacilityFormProps {
  initialData?: Facility;
  onSubmit: (data: FacilityFormValues) => void;
  onCancel: () => void;
}

export const FacilityForm = ({ initialData, onSubmit, onCancel }: FacilityFormProps) => {
  const form = useForm<FacilityFormValues>({
    resolver: zodResolver(facilitySchema) as any,
    defaultValues: initialData || {
      name: "",
      facility_code: "",
      license_number: "",
      target_state: "",
      phone_number: "",
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit as any)} className="space-y-4">
        <FormField
          control={form.control as any}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Name</FormLabel>
              <FormControl>
                <Input placeholder="Enter facility name" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control as any}
          name="facility_code"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Facility Code</FormLabel>
              <FormControl>
                <Input placeholder="e.g. NHMS-CA-01" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control as any}
          name="license_number"
          render={({ field }) => (
            <FormItem>
              <FormLabel>License Number</FormLabel>
              <FormControl>
                <Input placeholder="Enter license number" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <div className="grid grid-cols-2 gap-4">
          <FormField
            control={form.control as any}
            name="target_state"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Target State</FormLabel>
                <FormControl>
                  <Input placeholder="e.g. CA" maxLength={2} {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control as any}
            name="phone_number"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Phone Number</FormLabel>
                <FormControl>
                  <Input placeholder="Enter phone number" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>
        <div className="flex justify-end space-x-2 pt-4">
          <Button variant="outline" type="button" onClick={onCancel}>
            Cancel
          </Button>
          <Button type="submit">Save</Button>
        </div>
      </form>
    </Form>
  );
};
