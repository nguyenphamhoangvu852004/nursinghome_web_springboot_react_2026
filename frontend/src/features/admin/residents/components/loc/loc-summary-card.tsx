import type { LocClassificationResult } from "../../types/loc.type";


interface Props {
    result: LocClassificationResult | null;
}


export default function LocSummaryCard({ result }: Props) {


    if (!result) return null;


    const formatDate = (date?: string) => {

        if (!date) return "";

        return new Date(date).toLocaleDateString(
            "en-US",
            {
                year: "numeric",
                month: "long",
                day: "numeric"
            }
        );
    };


    return (

        <div className="space-y-8">


            {/* Header */}

            <div>

                <h1 className="text-2xl font-bold">

                    LOC Classification Result — {result.residentName}

                </h1>


                <p className="text-sm text-gray-500 mt-1">

                    From Assessment v3 • {formatDate(result.createdAt)}

                </p>


            </div>



            {/* Summary cards */}

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">


                {/* ADL */}

                <div className="
                    bg-white 
                    rounded-2xl 
                    border 
                    border-gray-200
                    p-6
                ">

                    <p className="text-sm text-gray-500 mb-2">

                        ADL Score

                    </p>


                    <div className="text-6xl font-bold">

                        {result.adlTotalScore}

                        <span className="
                            text-3xl 
                            text-gray-400
                        ">

                            /100

                        </span>

                    </div>


                </div>




                {/* Suggested LOC */}

                <div className="
                    md:col-span-2
                    bg-gradient-to-r 
                    from-yellow-50 
                    to-amber-50
                    border
                    border-yellow-200
                    rounded-2xl
                    p-6
                ">


                    <p className="
                        uppercase
                        tracking-widest
                        text-xs
                        text-amber-600
                        font-medium
                    ">

                        Suggested LOC

                    </p>



                    <h2 className="
                        text-3xl
                        font-bold
                        text-amber-700
                        mt-2
                    ">

                        {result.suggestedCareLevelCode}


                    </h2>



                    <p className="
                        text-lg
                        text-amber-800
                        mt-1
                    ">

                        {result.suggestedCareLevelName}


                    </p>


                </div>



            </div>


        </div>

    );

}