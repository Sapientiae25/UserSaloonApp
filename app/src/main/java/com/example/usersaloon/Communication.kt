package com.example.usersaloon

import com.google.android.gms.maps.model.LatLng

interface SearchDb { fun emptyDb(); fun searchDb(text: String) }
interface MoveMarker { fun move(location: LatLng) }
interface UpdateLocation { fun update(location: LatLng,address: String) }
