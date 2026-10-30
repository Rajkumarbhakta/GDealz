package com.rkbapps.gdealz.ui.screens.free_game_details

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.rkbapps.gdealz.R
import com.rkbapps.gdealz.models.Giveaway
import com.rkbapps.gdealz.ui.composables.ErrorScreen
import com.rkbapps.gdealz.ui.screens.steam_details.is_there_any_deal.HeroButton
import com.rkbapps.gdealz.ui.tab.deals.composables.DealPoster
import com.rkbapps.gdealz.ui.tab.deals.composables.DiscountBadge
import com.rkbapps.gdealz.ui.tab.free.parseApiDate
import com.rkbapps.gdealz.ui.tab.free.rememberNow
import com.rkbapps.gdealz.ui.tab.free.storeName
import com.rkbapps.gdealz.ui.theme.BrandShapes
import com.rkbapps.gdealz.ui.theme.PriceTextStyles
import com.rkbapps.gdealz.ui.theme.extendedColors
import java.text.SimpleDateFormat

private val PageSidePadding = 16.dp

/** GamerPower sends instructions as numbered lines; the number is drawn by the step itself. */
private fun Giveaway.claimSteps(): List<String> =
    instructions.split(Regex("\\r?\\n"))
        .map { it.trim().replace(Regex("^\\d+[.)]\\s*"), "") }
        .filter { it.isNotEmpty() }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FreeGameDetailsScreenExpressive(
    navController: NavHostController,
    viewModel: FreeGameDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.giveaway.collectAsStateWithLifecycle()
    val giveaway = state.data
    val colors = MaterialTheme.colorScheme

    if (giveaway == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface),
            contentAlignment = Alignment.Center
        ) {
            if (state.error != null) {
                ErrorScreen(message = state.error ?: stringResource(R.string.error_occurred))
            } else {
                ContainedLoadingIndicator(modifier = Modifier.size(64.dp))
            }
        }
        return
    }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val now = rememberNow()
    val end = remember(giveaway.endDate) { parseApiDate(giveaway.endDate) }
    val published = remember(giveaway.publishedDate) { parseApiDate(giveaway.publishedDate) }
    val hasEnded = end != null && end <= now
    val dateFormat = remember { SimpleDateFormat("EEE, d MMM yyyy", java.util.Locale.getDefault()) }
    val notAvailable = stringResource(R.string.not_available)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HeroButton(
                            icon = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            onClick = { navController.navigateUp() }
                        )
                        HeroButton(
                            icon = Icons.Rounded.Share,
                            contentDescription = stringResource(R.string.share),
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND)
                                    .setType("text/plain")
                                    .putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${giveaway.title}\n${giveaway.gamerPowerUrl}"
                                    )
                                context.startActivity(Intent.createChooser(intent, null))
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                )
            )
        }
    ) {  innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Hero(
                    imageUrl = giveaway.image,
                    onBack = { navController.navigateUp() },
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND)
                            .setType("text/plain")
                            .putExtra(
                                Intent.EXTRA_TEXT,
                                "${giveaway.title}\n${giveaway.gamerPowerUrl}"
                            )
                        context.startActivity(Intent.createChooser(intent, null))
                    }
                )
                TitleBlock(giveaway)
                if (end != null) {
                    Countdown(endMillis = end, publishedMillis = published, now = now)
                }
                KeyFacts(
                    where = giveaway.platforms,
                    type = giveaway.type,
                    published = published?.let(dateFormat::format) ?: notAvailable,
                    ends = end?.let(dateFormat::format) ?: notAvailable,
                )
                HowToClaim(
                    steps = remember(giveaway.instructions) { giveaway.claimSteps() },
                    claimed = giveaway.isClaimed
                )
                About(giveaway.description)
            }
            ClaimActions(
                store = giveaway.storeName(),
                claimed = giveaway.isClaimed,
                hasEnded = hasEnded,
                onClaim = {
                    try {
                        uriHandler.openUri(giveaway.openGiveawayUrl)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, R.string.unable_to_open_website, Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                onToggleClaimed = { viewModel.setClaimed(giveaway, !giveaway.isClaimed) },
            )
        }
    }
}

@Composable
private fun Hero(imageUrl: String, onBack: () -> Unit, onShare: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
    ) {
        DealPoster(url = imageUrl, modifier = Modifier.fillMaxSize())
        DiscountBadge(
            cut = 100,
            size = 96.dp,
            textStyle = PriceTextStyles.medium,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        )
    }
}

@Composable
private fun StatusChip(text: String, container: Color, content: Color) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMediumEmphasized,
        color = content,
        modifier = Modifier
            .background(container, MaterialTheme.shapes.small)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

@Composable
private fun TitleBlock(giveaway: Giveaway) {
    val colors = MaterialTheme.colorScheme
    val extended = MaterialTheme.extendedColors
    val store = giveaway.storeName()
    val worth = giveaway.worth.takeIf { it.isNotBlank() && it != "N/A" }

    Column(
        modifier = Modifier.padding(horizontal = PageSidePadding + 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusChip(giveaway.type, colors.surfaceContainerHigh, colors.onSurface)
            if (giveaway.isClaimed) {
                StatusChip(stringResource(R.string.claimed), extended.dealContainer, extended.onDealContainer)
            } else {
                StatusChip(stringResource(R.string.not_claimed_yet), extended.giftContainer, extended.onGiftContainer)
            }
        }
        Text(
            text = giveaway.title,
            style = MaterialTheme.typography.displayMediumEmphasized.copy(fontSize = 40.sp, lineHeight = 44.sp),
        )
        val freeOn = stringResource(R.string.free_on, store)
        val usually = worth?.let { stringResource(R.string.usually_price, it) }
        Text(
            text = buildAnnotatedString {
                append(freeOn)
                if (usually != null && worth != null) {
                    append(" · ")
                    val start = length + usually.indexOf(worth)
                    append(usually)
                    addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), start, start + worth.length)
                }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun countdownText(endMillis: Long, now: Long): String {
    val minutes = (endMillis - now) / 60_000
    val days = minutes / (60 * 24)
    val hours = (minutes / 60) % 24
    return when {
        minutes <= 0 -> stringResource(R.string.giveaway_ended)
        days > 0 -> "${stringResource(R.string.duration_days_hours, days, hours)} ${stringResource(R.string.duration_minutes, minutes % 60)}"
        hours > 0 -> stringResource(R.string.duration_hours_minutes, hours, minutes % 60)
        else -> stringResource(R.string.duration_minutes, minutes)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Countdown(endMillis: Long, publishedMillis: Long?, now: Long) {
    val gift = MaterialTheme.extendedColors
    val format = SimpleDateFormat("EEE, d MMM yyyy · h:mm a", LocalLocale.current.platformLocale)
    // Time left in the offer, draining as the deadline approaches.
    val remaining = if (publishedMillis != null && endMillis > publishedMillis) {
        ((endMillis - now).toFloat() / (endMillis - publishedMillis)).coerceIn(0f, 1f)
    } else 1f

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = gift.giftContainer,
        contentColor = gift.onGiftContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PageSidePadding)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.offer_ends_in).uppercase(),
                style = MaterialTheme.typography.labelLargeEmphasized,
                color = gift.giftContent,
            )
            Text(
                text = countdownText(endMillis, now),
                style = PriceTextStyles.large.copy(fontSize = 34.sp, lineHeight = 36.sp),
            )
            LinearWavyProgressIndicator(
                progress = { remaining },
                color = gift.giftContent,
                trackColor = gift.giftContent.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = format.format(endMillis), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun KeyFacts(where: String, type: String, published: String, ends: String) {
    Column(
        modifier = Modifier.padding(horizontal = PageSidePadding),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Fact(
                label = stringResource(R.string.where),
                value = where,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier.weight(1f)
            )
            Fact(
                label = stringResource(R.string.type),
                value = type,
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 24.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier.weight(1f)
            )
        }
        Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Fact(
                label = stringResource(R.string.published),
                value = published,
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 24.dp, bottomEnd = 8.dp),
                modifier = Modifier.weight(1f)
            )
            Fact(
                label = stringResource(R.string.offer_ends),
                value = ends,
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 24.dp),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun Fact(label: String, value: String, shape: RoundedCornerShape, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surfaceContainer, shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMediumEmphasized,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = value, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun HowToClaim(steps: List<String>, claimed: Boolean) {
    val finalTitle = stringResource(R.string.claim_step_mark_title)
    val finalBody = stringResource(R.string.claim_step_mark_body)
    val extended = MaterialTheme.extendedColors
    val colors = MaterialTheme.colorScheme
    val count = steps.size + 1

    Column(modifier = Modifier.padding(horizontal = PageSidePadding)) {
        Text(
            text = stringResource(R.string.how_to_claim),
            style = MaterialTheme.typography.headlineSmallEmphasized,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 12.dp)
        )
        steps.forEachIndexed { index, step ->
            ClaimStep(
                number = index + 1,
                title = step,
                body = null,
                chip = if (index == 0) extended.giftFill else colors.surfaceContainerHigh,
                onChip = if (index == 0) extended.onGiftFill else colors.onSurface,
                hasNext = true,
            )
        }
        ClaimStep(
            number = count,
            title = finalTitle,
            body = finalBody,
            chip = if (claimed) extended.dealFill else colors.surfaceContainerHigh,
            onChip = if (claimed) extended.onDealFill else colors.onSurface,
            hasNext = false,
        )
    }
}

@Composable
private fun ClaimStep(number: Int, title: String, body: String?, chip: Color, onChip: Color, hasNext: Boolean) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(chip, BrandShapes.score),
                contentAlignment = Alignment.Center
            ) {
                Text(text = number.toString(), style = PriceTextStyles.small, color = onChip)
            }
            if (hasNext) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            if (body != null) {
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun About(description: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var overflows by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = PageSidePadding + 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = stringResource(R.string.about), style = MaterialTheme.typography.headlineSmallEmphasized)
        Text(
            text = AnnotatedString(description),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 23.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expanded) overflows = it.hasVisualOverflow },
            modifier = Modifier.animateContentSize()
        )
        if (overflows) {
            TextButton(onClick = { expanded = !expanded }) {
                Text(
                    text = stringResource(if (expanded) R.string.show_less else R.string.read_more),
                    style = MaterialTheme.typography.labelLargeEmphasized,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ClaimActions(
    store: String,
    claimed: Boolean,
    hasEnded: Boolean,
    onClaim: () -> Unit,
    onToggleClaimed: () -> Unit,
) {
    val extended = MaterialTheme.extendedColors
    val colors = MaterialTheme.colorScheme
    val corner by animateDpAsState(
        targetValue = if (claimed) 16.dp else 26.dp,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    Surface(
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        color = colors.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(
                    start = PageSidePadding,
                    end = PageSidePadding,
                    top = 14.dp,
                    bottom = 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onClaim,
                enabled = !hasEnded,
                shapes = ButtonDefaults.shapes(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = extended.giftFill,
                    contentColor = extended.onGiftFill
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
            ) {
                Text(
                    text = if (store.isNotEmpty()) stringResource(R.string.claim_on, store)
                    else stringResource(R.string.claim),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(18.dp)
                )
            }
            Button(
                onClick = onToggleClaimed,
                shape = RoundedCornerShape(corner),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (claimed) extended.dealContainer else colors.surfaceContainerHighest,
                    contentColor = if (claimed) extended.onDealContainer else colors.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Text(
                    text = stringResource(if (claimed) R.string.claimed_tap_to_undo else R.string.mark_claimed),
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
