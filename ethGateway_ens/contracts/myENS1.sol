pragma solidity ^0.4.15;

contract myENS1 {
  // names
  mapping(string => address) list; 

  function own_addr( string name ) returns (bool){
	  list[name] = msg.sender;
	  return true;
  }

  function who_is( string name ) constant returns (address){
	  return list[name];
  }

}
