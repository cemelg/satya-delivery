Intra Bank Payment Transfer System
===================================================================================================================
Account balance API :: GET

/accounts/{accNo}/balance

http://localhost:8080/accounts/111/balance

===================================================================================================================
Mini-statment API :: GET


/acounts/{accNo}/statements/mini

http://localhost:8080/accounts/111/statements/mini

===================================================================================================================

Balance Transfer API :: POST


/accounts/balanceTransfer

http://localhost:8080/accounts/balanceTransfer

content-type : Json

{
    "accountNo":333 ,
    "befinicaryNo":111 ,
    "amountToTransfer": 1.0
}

===================================================================================================================

MISC APIs

Get All accounts :: GET

/accounts/

http://localhost:8080/accounts/


Add Account api

/accounts/addAccount

http://localhost:8080/accounts/addAccount

content-type : Json

{
    "accountId": 444,
    "balance": 100.0,
    "currency": "USD"
}


Delete account api

/accounts/{accNo}/close

http://localhost:8080/accounts/444/close


===================================================================================================================

H2 Embedded DB console url

http://localhost:8080/h2-console

===================================================================================================================
