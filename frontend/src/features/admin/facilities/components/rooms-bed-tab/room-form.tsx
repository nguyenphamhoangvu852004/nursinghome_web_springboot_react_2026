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
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import type { Room } from "@/services/rooms-api";
import type { Facility } from "@/services/facilities-api";

const roomSchema = z.object({
  room_number: z.string().min(1, "Room number is required"),
  room_type: z.enum(["PRIVATE", "SEMI_PRIVATE", "WARD"]),
  facility_id: z.string().min(1, "Facility is required"),
});

type RoomFormValues = {
  room_number: string;
  room_type: "PRIVATE" | "SEMI_PRIVATE" | "WARD";
  facility_id: string;
};

interface RoomFormProps {
  initialData?: Room;
  facilities: Facility[];
  onSubmit: (data: RoomFormValues) => void;
  onCancel: () => void;
}

export const RoomForm = ({ initialData, facilities, onSubmit, onCancel }: RoomFormProps) => {
  const form = useForm<RoomFormValues>({
    resolver: zodResolver(roomSchema) as any,
    defaultValues: initialData ? {
      room_number: initialData.room_number,
      room_type: initialData.room_type,
      facility_id: String(initialData.facility_id),
    } : {
      room_number: "",
      room_type: "PRIVATE",
      facility_id: "",
    },
  });

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit as any)} className="space-y-4">
        <FormField
          control={form.control as any}
          name="room_number"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Room Number</FormLabel>
              <FormControl>
                <Input placeholder="Enter room number" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control as any}
          name="room_type"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Room Type</FormLabel>
              <Select onValueChange={field.onChange} defaultValue={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select room type" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  <SelectItem value="PRIVATE">Private</SelectItem>
                  <SelectItem value="SEMI_PRIVATE">Semi-Private</SelectItem>
                  <SelectItem value="WARD">Ward</SelectItem>
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control as any}
          name="facility_id"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Facility</FormLabel>
              <Select onValueChange={field.onChange} defaultValue={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select a facility" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {facilities.map(f => (
                    <SelectItem key={f.id} value={String(f.id)}>{f.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          )}
        />
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
