import Text from "../ui/Text";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import Flag from "../ui/flag";
import { Link } from "react-router";
import type {
  CarePlan,
  ResidentDefinition,
  ResidentInfo,
} from "@/services/care-plan/care-plan-types";
import { formatOffsetDateTimeToDate } from "../utils/time-utils";
import {
  getCarePlanLOCTierStyle,
  getCarePlanStatusStyle,
} from "../utils/style";
import { Loader2 } from "lucide-react";

type CarePlanTableProps = {
  carePlans: CarePlan[];
  isLoading: boolean;
};

export default function CarePlanTable(props: CarePlanTableProps) {
  const getResidentInfo = (carePlan: CarePlan) => {
    const definition: ResidentDefinition = carePlan.definition;
    const residentInfo: ResidentInfo = carePlan.resident;

    return (
      residentInfo.fullname + " " + "." + " " + definition.room + definition.bed
    );
  };

  if (props.isLoading) {
    return (
      <div className="flex justify-center items-center h-80">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }
  return (
    <div className="mt-12">
      <Table className=" border-2 border-gray-l00 rounded-lg p-4">
        {/* <TableCaption>A list of your recent invoices.</TableCaption> */}
        <TableHeader className="bg-gray-300 ">
          <TableRow className="font-bold font text-lg ">
            <TableHead className="w-[180ppx] text-left text-gray-600">
              {/* Resident */}
              <Text>Resident</Text>
            </TableHead>
            <TableHead className="text-left  text-gray-600">
              <Text>LOC Tier</Text>
            </TableHead>
            <TableHead className="text-left text-gray-600">
              <Text>Status</Text>
            </TableHead>
            <TableHead className="text-left text-gray-600">
              <Text>Last Review</Text>
            </TableHead>
            <TableHead className="text-left text-gray-600">
              <Text>Next Review</Text>
            </TableHead>
            <TableHead className="text-left text-gray-600">
              <Text>Assinged</Text>
            </TableHead>
            <TableHead className="text-left">Action</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody className="bg-white">
          {props.carePlans.map((carePlan) => (
            <TableRow key={carePlan.id}>
              <TableCell className="font-medium">
                <Text className="text-black">{getResidentInfo(carePlan)}</Text>
              </TableCell>

              <TableCell className="text-left">
                <Flag
                  title={`Tier ${carePlan.LOCTier}`}
                  className={`rounded-full ${getCarePlanLOCTierStyle(carePlan).className}`}
                ></Flag>
              </TableCell>

              <TableCell className="text-left">
                <Flag
                  title={carePlan.status}
                  className={`rounded-full ${getCarePlanStatusStyle(carePlan).className}`}
                />
              </TableCell>

              <TableCell className="text-left">
                <Text>
                  {formatOffsetDateTimeToDate(carePlan.lastReviewedDateTime)}
                </Text>
              </TableCell>

              <TableCell className="text-left">
                <Text
                  className={
                    carePlan.nextReviewDateTime === "Overdue"
                      ? "text-blue-600 font-medium font-bold"
                      : ""
                  }
                >
                  {formatOffsetDateTimeToDate(carePlan.nextReviewDateTime)}
                </Text>
              </TableCell>

              <TableCell className="text-left">
                <Text>{carePlan.createdBy.fullname}</Text>
              </TableCell>

              <TableCell className="text-left cursor-pointer">
                <TableCell className="text-left">
                  <Link to={`/admin/care-plans/${carePlan.id}`}>
                    <Text className="text-blue-700 font-bold ">View</Text>
                  </Link>
                </TableCell>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}
