interface StatusBadgeProps{
    status:string;
    type?:"status"|"2fa";
}

export function StatusBadge({

    status,
    type="status"

}:StatusBadgeProps){

    let className="";

    if(type==="2fa"){

        switch(status){

            case "Enabled":

                className="border border-green-300 bg-green-100 text-green-700";
                break;

            case "Not-set":

                className="border border-yellow-300 bg-yellow-100 text-yellow-700";
                break;

            default:

                className="border border-gray-300 bg-gray-100 text-gray-600";
        }

    }else{

        switch(status){

            case "Active":

                className="border border-green-300 bg-green-100 text-green-700";
                break;

            case "Invited":

                className="border border-yellow-400 bg-yellow-100 text-yellow-700";
                break;

            case "Suspended":

                className="border border-red-300 bg-red-100 text-red-700";
                break;

            case "Deactivated":

                className="border border-gray-300 bg-gray-100 text-gray-600";
                break;

            default:

                className="border border-gray-300 bg-gray-100 text-gray-600";
        }

    }

    return(

        <span
            className={`inline-flex min-w-[82px] justify-center rounded-full px-3 py-1 text-xs font-semibold ${className}`}
        >
            {status}
        </span>

    );

}