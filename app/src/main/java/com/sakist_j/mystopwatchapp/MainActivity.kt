package com.sakist_j.mystopwatchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sakist_j.mystopwatchapp.ui.theme.MyStopwatchAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyStopwatchAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyStopwatch()
                }
            }
        }
    }
}


//TODO: Logic
//Most of the codes here comes from: https://akjaw.com/kotlin-coroutine-flow-stopwatch-part1/
//define state in stopwatch app
sealed class StopwatchState(){
    data class Paused(
        val elapsedTime: Long //TODO: explain why elapsed time's type is Long
    ): StopwatchState()
    data class Running(
        val startTime:Long,
        val elapsedTime: Long
    ): StopwatchState()
    // "The stop state is not required because if the stopwatch is stop, state is not change"
}

//timestamp provider
interface TimeStampProvider{
    fun getMilliseconds(): Long
}

//state manipulation
class StopwatchStateCalculator(
    // create variable to handle state
    private val timeStampProvider: TimeStampProvider,
    private val elapsedTimeCalculator : ElapsedTimerCalculator,
){
    //calculate running state time
    fun calculateRunningState(oldState: StopwatchState): StopwatchState.Running =
        when(oldState){
            is StopwatchState.Running -> oldState
            is StopwatchState.Paused -> {
                StopwatchState.Running(
                    startTime = timeStampProvider.getMilliseconds(),
                    elapsedTime = oldState.elapsedTime
                )
            }
        }
    //calculate pause state
    fun calculateStopState(oldState: StopwatchState): StopwatchState.Paused =
        when(oldState){
            is StopwatchState.Running -> {
                val elapsedTime = elapsedTimeCalculator.calculate(oldState)
                StopwatchState.Paused(elapsedTime = elapsedTime)
            }
            is StopwatchState.Paused -> oldState
        }
}

// Calculating the elapsed time
class ElapsedTimerCalculator(
    private val timeStampProvider: TimeStampProvider,
){
    //implement calculate function
    fun calculate(state: StopwatchState.Running): Long{
        //get time stamp
        val currentTimeStamp = timeStampProvider.getMilliseconds()
        val timePassedSinceStart = if(currentTimeStamp > state.startTime){
            currentTimeStamp - state.startTime
        } else{
            0
        }
        return timePassedSinceStart + state.elapsedTime
    }
}

//Formatting stopwatch time
internal class TimeStampMillisecondsFormatter(){
    companion object {
        const val DEFAULT_TIME = "00:00:00"
    }
    fun format(timestamp: Long): String{
        val millisecondsFormatted = (timestamp % 1000).pad(2) // I can use padStart() or PadEnd() instead
        val seconds = timestamp / 1000
        val secondsFormatted = (seconds % 60).pad(2)
        val minutes = seconds / 60
        val minutesFormatted = (minutes % 60).pad(2)
        val hours = minutes / 60
        return if (hours > 0){
            val hoursFormatted = (minutes % 60).pad(2)
            "$hoursFormatted:$minutesFormatted:$secondsFormatted"
        } else{
            "$millisecondsFormatted:$secondsFormatted:$millisecondsFormatted"
        }
    }
    private fun Long.pad(desireLength: Int) = this.toString().padStart(desireLength,'0')
}





//TODO: UI -> Timer, Table, and Buttons
//TODO: Show Timer in format 00:00:00
@Composable
private fun TimerUI(modifier: Modifier = Modifier,
@StringRes text:Int){
    Text(text = stringResource(text))
}
//TODO: Show Table
@Composable
private fun TableUI(modifier: Modifier = Modifier,
@StringRes attempt: Int,
@StringRes rank: Int,
@StringRes time: Int){

    //TODO: Material Design table -> render when having data
    //TODO: Starter Table
    Row(){
        Column(modifier = modifier.weight(1f)) {
            Text(text = stringResource(id = attempt))
            Text(text = "1st")
        }
        Column(modifier = modifier.weight(1f)) {
            Text(text = stringResource(id = rank))
            Text(text = "1st")
        }
        Column() {
            Text(text = stringResource(id = time))
            Text(text = "00:00:00")
        }
    }
}
@Composable
private fun ButtonUI(modifier: Modifier = Modifier,
                     @StringRes text1: Int,
                     @StringRes text2: Int){
    //TODO: Two elevatedButton in one row
    Row(modifier = modifier
        .paddingFromBaseline(10.dp)
        .padding(12.dp)
    ) {
        Column(modifier = modifier
            .weight(1f)) {
            ElevatedButton(onClick = { /*TODO*/ }) {
                Text(text = stringResource(id = text1))
            }
            }
        ElevatedButton(onClick = { /*TODO*/ }) {
            Text(text = stringResource(id = text2))
        }
    }

}


//TODO: Stopwatch
@Composable
fun MyStopwatch(modifier: Modifier = Modifier){
    Surface(){
        Column(modifier = modifier.padding(vertical = 8.dp)){
            Row(modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center) {
                TimerUI(text = R.string.timer)
            }
            Row(){
                TableUI(
                    attempt = R.string.attempt,
                    rank = R.string.rank,
                    time = R.string.time)
            }
            Row(modifier = Modifier
                .padding(12.dp)) {
                ButtonUI(text1 = R.string.start, text2 = R.string.reset)
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyStopwatchAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MyStopwatch()
        }
    }
}
@Preview(showBackground = true)
@Composable
fun TimerUIPreview() {
    MyStopwatchAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            TimerUI(text = R.string.timer)

        }
    }
}
@Preview(showBackground = true)
@Composable
fun TableUIPreview(){
    Surface(){
        TableUI(
            attempt = R.string.attempt,
            rank = R.string.rank,
            time = R.string.time
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ButtonUIPreview(){
    Surface(){
        ButtonUI(text1 = R.string.start, text2 = R.string.reset)
    }
}