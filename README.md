# RohlikSample

I used compose library for the UI: you can see my views inside compose package and how they are getting used by the classes defined in ui package

For backend communication i have everything implemented but for now it just reads a json file from assets folder
You can see ItemizatonEntryDTO and NetworkReponse classes inside data package

Network package has the necassary interface and implementation of the repository i defined for the backend communication

DB package has database classes inside for now i just put them there along with a synchronization logic inorder to implement an offline first app
//TODO change direct backend call in the viewModel with this sync logic

Thus, Synchronization package contains NetSyncManager interface which is extended by the ItemizationListNetSynchManager its task is to get locally changed data
and synchronize it with our imaginaary backend.
Those 2 layers (db and backend) use different objects so that both of them can be easily replaced, after retrieving the data i am mapping one to the other.

So in the end after i will also finish the last part stated as TODO above:
Ui will make the changes on db layer first, change the state of the objects - in the synchronization cycle SynchManager will go through this data and chheck the state of the objects
And upload these data to backend first, then import aand fetch latest data from backend (because of user access writes and etc there could be changes in the backend)

Namings can be seen unfamiliar but these are the data i was dealing at work so i used the same namings out of lazyness: Like ExpenseReportsApi, ItemizationEntry etc


