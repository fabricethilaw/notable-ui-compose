package com.notableui.avatar

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Create StatusBadge class with the required information to display a status badge.
 *
 * The common pattern of displaying the user's status is to spot a round indicator at the bottom right.
 * - Preferably use green for online, gray for offline status
 * - Use filled shape when online, a stroke or no shape when offline.
 *
 * Three styles are available to represent the offline status:
 * - [Style.OFFLINE_INDICATOR_INSIDE] display an inner stroke shape in the badge.
 * - [Style.OFFLINE_INDICATOR_ON_EDGE] display an a stroke around the badge.
 * - [Style.NO_OFFLINE_INDICATOR] hide the badge.
 *
 * @param isOnline Set true to notify user is online, false otherwise.
 * @param offlineColor The color to set the stroke shape when [isOnline] is false and [style] is not [Style.NO_OFFLINE_INDICATOR].
 * @param onlineColor The color to set the filled shape when [isOnline] is true.
 * @param style Set the style for the badge when it is in offline state. Default is [Style.NO_OFFLINE_INDICATOR]
 * @param size The size of the badge. Default is 20 dp.
 */
public class AvatarBadge(
  public val isOnline: Boolean,
  public val offlineColor: Color = DefaultOfflineColor,
  public val onlineColor: Color = DefaultOnlineColor,
  public val style: Style = Style.OFFLINE_INDICATOR_INSIDE,
  public val size: Dp = 20.dp
) {
    public companion object {
        public val DefaultOnlineColor: Color = Color(0xff51ca31)
        public val DefaultOfflineColor: Color = Color(0xff5f6368)
    }

    public enum class Style {
        NO_OFFLINE_INDICATOR, OFFLINE_INDICATOR_INSIDE, OFFLINE_INDICATOR_ON_EDGE
    }
}