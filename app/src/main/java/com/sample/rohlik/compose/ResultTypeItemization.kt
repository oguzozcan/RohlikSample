package com.sample.rohlik.compose

import com.sample.rohlik.R

sealed class ResultTypeItemization(val messageResource: Int)

class INIT_LOAD : ResultTypeItemization(-1)
class IN_PROGRESS :
    ResultTypeItemization(R.string.itemizationlist_loading)

class DELETION_IN_PROGRESS :
    ResultTypeItemization(R.string.itemization_delete_selected)

class DELETION_SUCCESSFUL :
    ResultTypeItemization(R.string.itemization_delete_successful)

class DELETION_FAILED :
    ResultTypeItemization(R.string.itemization_delete_failed)

class SUCCESS :
    ResultTypeItemization(R.string.itemizationlist_loaded)

class NO_RESULT :
    ResultTypeItemization(R.string.itemization_empty)

class SERVER_ERROR :
    ResultTypeItemization(R.string.itemization_error)

class NETWORK_ERROR :
    ResultTypeItemization(R.string.itemization_error)

class UNKNOWN_ERROR :
    ResultTypeItemization(R.string.itemization_error)