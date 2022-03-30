package com.example.usersaloon

import com.google.android.gms.maps.model.LatLng

interface MoveMarker { fun move(location: LatLng) }
interface CloseSearch { fun closeSearch() }
interface UpdateLocation { fun update(location: LatLng,address: String) }
