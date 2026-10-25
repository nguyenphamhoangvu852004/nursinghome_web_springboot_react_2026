export default function LocLevelReference(){


const levels=[
{
code:"Level 1",
desc:"Independent"
},
{
code:"Level 2",
desc:"Limited Assistance"
},
{
code:"Level 3",
desc:"Extensive Assistance"
},
{
code:"Level 4",
desc:"Total Assistance"
}
];


return (

<div className="
grid grid-cols-4 gap-3 mt-8
">

{
levels.map(level=>(

<div
key={level.code}
className="
bg-white
border
rounded-xl
p-4
text-center
"
>

<p className="font-semibold">
{level.code}
</p>


<p className="text-xs text-gray-500">
{level.desc}
</p>


</div>

))
}

</div>

)

}