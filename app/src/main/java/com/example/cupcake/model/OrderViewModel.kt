package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val PRICE_PER_CUPCAKE = 2.00
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

class OrderViewModel: ViewModel() {
    // Private properties and respective public backing properties to access the private values
    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private val _flavor = MutableLiveData<String>()
    val flavor: LiveData<String> = _flavor

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _price = MutableLiveData<Double>()
    val price: LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    val dateOptions = getPickupOptions()

    // initial values of the properties
    init {
        resetOrder()
    }

    fun setQuantity(numberOfCupCakes: Int) {
        _quantity.value = numberOfCupCakes
        updatePrice()
    }

    fun setFlavor(desiredFlavor: String) {
        _flavor.value = desiredFlavor
    }

    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    fun hasNoFlavorSet(): Boolean {
        return _flavor.value.isNullOrEmpty()
    }

    private fun getPickupOptions(): List<String> {
        val options = mutableListOf<String>()
        //E - day name, MMM - month in 3 letters, d for day....Tue Dec 10
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        // Calender instance
        val calender = Calendar.getInstance()

        // Create a list of dates starting with the current date and the following 3 dates
        repeat(4) {
            // Add date to the list
            options.add(formatter.format(calender.time))
            // Increment calender by 1 day
            calender.add(Calendar.DATE, 1)
        }
        return options
    }

    fun resetOrder() {
        _quantity.value = 0
        _flavor.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0
    }

    // Helper method to calculate the price
    private fun updatePrice() {
        var calculatePrice = (quantity.value ?: 0).times(PRICE_PER_CUPCAKE)
        // If the user selected the first option (today) for pickup, add the surcharge
        if (_date.value == dateOptions[0]) {
            calculatePrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatePrice
    }
}