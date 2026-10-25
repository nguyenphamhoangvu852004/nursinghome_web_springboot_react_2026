import { Button } from "@/components/ui/button";

export type FlagProps = {
  className?: string;
  title: string;
};

export default function Flag(props: FlagProps) {
  return (
    <div>
      <div
        className={`flex min-h-[36px] max-w-max border-2 border-blue-300 text-center items-center justify-center pl-[8px] pr-[8px] rounded-full bg-blue-200 text-blue-800 ${props.className}`}
      >
        {props.title}
      </div>
    </div>
  );
}
