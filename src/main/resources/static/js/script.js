console.log("this is java script file")
const toggleSidebar=()=>{
    if($(".sidebar").is(":visible")){
        $(".sidebar").css("display","none")
        $(".content").css("margin-left","2%")
        $(".profile-page .bg div").css("margin-left","2%")
        $(".home-page .bg div").css("margin-left","2%")

    }else{
        $(".sidebar").css("display","block")
        $(".content").css("margin-left","20%")
        $(".profile-page .bg div").css("margin-left","20%")
        $(".home-page .bg div").css("margin-left","20%")
        $(".home-page .content").css("margin-left","0")
        $(".profile-page .content").css("margin-left","0")



    }
};


const likeb=document.getElementById('likeb');
const likecount=document.getElementById('likecount');
let islike=false;
let count=0;
const likefunc=()=>{
    if(islike){
        count--;
        likeb.classList.remove("like");
        likeb.style.color='';
        likeb.style.fontSize='24px';

    }else{
        count++;
        likeb.classList.add("like");
        likeb.style.color='blue';
        likeb.style.fontSize='30px';
    }
    islike=!islike;
    likecount.innerText = count;
    updateLike();
}

