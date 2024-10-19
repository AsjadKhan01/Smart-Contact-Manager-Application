/**
 * 
 */

 console.log("this is js file which is work...");
function getOtp(){
	console.log("otp function called");
	//window.location="/user/otp";
};

function deleteContact(cid){
	swal({
		  title: "Are you sure?",
		  text: "Once deleted, you will not be able to recover this Contact!",
		  icon: "warning",
		  buttons: true,
		  dangerMode: true,
		})
		.then((willDelete) => {
		  if (willDelete) {
			  console.log("deleted is is : "+cid)
			  window.location="/user/delete/"+cid;
		    swal("Poof! Your Contact has been deleted!", {
		      icon: "success",
		    });
		  } else {
		    swal("Your Contact is safe!");
		  }
		})
	};
	
function recycleContact(cId){
	 console.log("recyled is : "+cId)
	 window.location="/user/recylce/"+cId;
};

function prDeleteContact(cId){
	console.log("Parmanent Delete contact !!"+cId);
	window.location="/user/prDeleteCont/"+cId;
};

 const toggleSidebar =() =>{
    if($(".sidebar").is(":visible")){
        //true
        //band karna hai
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","2%");
        $(".asj").css("display","block")

    }else{
        //false
        //show karna hai
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
        $(".asj").css("display","none")
    }
 };

 const search = ()=>{
    let query = $("#search-input").val();

    if(query == ''){
        $(".search-result").hide();
    }else{
        //search
        console.log(query);
        let url = `http://localhost:9090/search/${query}`;

        fetch(url).then((response)=>{
            return response.json();
        }).then((data)=>{
            //data
            console.log(data);
            
            let text =`<div class='list-group'>`;
            
            data.forEach((contact)=>{
				text+= `<a href='/user/${contact.cId}/show-contacts' class='list-group-item list-group-item-action'>${contact.name}</a>`
			});
            
            text+=`</div>`;
            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
 };
 