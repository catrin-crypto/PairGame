package catrin.dev.pairgame.data

import android.graphics.drawable.Drawable

enum class GameCard(private var image: Drawable? = null) {
    Doorbell,
    DownhillSkiing,
    DryCleaning,
    EdgeSensor,
    ElectricBike,
    ElectricCar,
    EmojiNature,
    Events,
    Favorite,
    HeartBroken;

    fun setImage(image: Drawable?) {
        this.image = image
    }

    fun getImage() = image

    companion object {
        fun checkImagesAreNotNull() {
            GameCard.values().forEach { card ->
                if (card.image == null)
                    throw RuntimeException("$card.image == null!")
            }
        }
    }
}
