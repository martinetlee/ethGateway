pragma solidity ^0.4.15;
  
contract myENS2 {

	struct Record {
		address owner;
		uint ttl;			// for ttl info in dns
		string resolve_to;	// for this prototype, assume it is in the correct form IPv4
	}

  	// names
  	mapping(string => Record) list;

    // code snippet from ens
    // Permits modifications only by the owner of the specified node.
  	modifier only_owner(string node) {
  		require(list[node].owner == msg.sender);
  		_;
    	//if (list[node].owner != msg.sender) throw;
		//_;
  	}

  	function own_addr( string name , string res_to) {
  		list[name].owner = msg.sender;
    	list[name].resolve_to = res_to;
  	}

  	function who_is( string name ) constant returns (string){
       	return list[name].resolve_to;
  	}

  	function mod_addr( string name, string res_to) only_owner(name){
  		list[name].resolve_to = res_to;
  	}

  	function change_owner( string name, address new_node) only_owner(name){
  		list[name].owner = new_node;
  	}
}

