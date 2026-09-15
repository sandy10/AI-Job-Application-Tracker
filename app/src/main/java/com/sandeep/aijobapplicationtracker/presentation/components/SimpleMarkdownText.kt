package com.sandeep.aijobapplicationtracker.presentation.components

import com.sandeep.aijobapplicationtracker.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val lines = text.split("\n")
    Column(modifier = modifier) {
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                // Add empty space for paragraph break
                Text(text = "", fontSize = 4.sp, modifier = Modifier.padding(bottom = 4.dp))
                continue
            }
            
            var isBullet = false
            var content = trimmed
            var headerLevel = 0
            
            if (trimmed.startsWith("# ")) {
                headerLevel = 1
                content = trimmed.substring(2).trim()
            } else if (trimmed.startsWith("## ")) {
                headerLevel = 2
                content = trimmed.substring(3).trim()
            } else if (trimmed.startsWith("### ")) {
                headerLevel = 3
                content = trimmed.substring(4).trim()
            } else if (trimmed.startsWith("#### ")) {
                headerLevel = 4
                content = trimmed.substring(5).trim()
            } else if (trimmed.startsWith("* ") || trimmed.startsWith("- ")) {
                isBullet = true
                content = trimmed.substring(2).trim()
            }
            
            val annotatedString = buildAnnotatedString {
                var currentIndex = 0
                val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
                val matches = boldRegex.findAll(content)
                
                for (match in matches) {
                    val startIndex = match.range.first
                    val endIndex = match.range.last + 1
                    val boldText = match.groupValues[1]
                    
                    // Add text before bold
                    if (currentIndex < startIndex) {
                        append(content.substring(currentIndex, startIndex))
                    }
                    
                    // Add bold text
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                        append(boldText)
                    }
                    
                    currentIndex = endIndex
                }
                
                // Add remaining text
                if (currentIndex < content.length) {
                    append(content.substring(currentIndex))
                }
            }
            
            val textStyle = when (headerLevel) {
                1 -> MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                2 -> MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                3 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                4 -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                else -> MaterialTheme.typography.bodyMedium
            }
            
            val textColor = if (headerLevel > 0) MaterialTheme.colorScheme.onSurface else color
            val bottomPadding = if (headerLevel > 0) 12.dp else 8.dp
            val topPadding = if (headerLevel > 0) 16.dp else 0.dp
            
            if (isBullet) {
                Row(modifier = Modifier.padding(bottom = 6.dp, start = 8.dp)) {
                    Text(
                        text = stringResource(R.string.string_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium,
                        color = color,
                        lineHeight = 22.sp
                    )
                }
            } else {
                Text(
                    text = annotatedString,
                    style = textStyle,
                    color = textColor,
                    lineHeight = if (headerLevel > 0) 30.sp else 22.sp,
                    modifier = Modifier.padding(top = topPadding, bottom = bottomPadding)
                )
            }
        }
    }
}
