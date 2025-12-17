let metrics = {
    total: 0,
    valid: 0,
    invalid: 0
};

function updateMetrics() {
    document.getElementById('totalValidations').textContent = metrics.total;
    document.getElementById('validCount').textContent = metrics.valid;
    document.getElementById('invalidCount').textContent = metrics.invalid;
    
    const rate = metrics.total > 0 
        ? Math.round((metrics.valid / metrics.total) * 100) 
        : 0;
    document.getElementById('successRate').textContent = rate + '%';
}

async function validateExpression() {
    const expression = document.getElementById('cronExpression').value.trim();
    const timezone = document.getElementById('timezone').value;
    const resultDiv = document.getElementById('validationResult');
    const previewSection = document.getElementById('previewSection');

    if (!expression) {
        showError(resultDiv, 'Please enter a cron expression');
        return;
    }

    try {
        const response = await fetch('/api/cron/validate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                expression: expression,
                timezone: timezone,
                previewCount: 5
            })
        });

        const data = await response.json();
        metrics.total++;

        if (data.valid) {
            metrics.valid++;
            showSuccess(resultDiv, data);
            showNextExecutions(data.nextExecutions);
            previewSection.style.display = 'block';
        } else {
            metrics.invalid++;
            showError(resultDiv, data.errorMessage);
            previewSection.style.display = 'none';
        }

        updateMetrics();

    } catch (error) {
        metrics.total++;
        metrics.invalid++;
        showError(resultDiv, 'Network error: ' + error.message);
        updateMetrics();
    }
}

function showSuccess(resultDiv, data) {
    resultDiv.className = 'result-box success';
    resultDiv.innerHTML = `
        <div class="label">✅ Valid Expression</div>
        <div class="value">${data.expression}</div>
        <div class="label">Human Readable:</div>
        <div class="value">${data.humanReadable}</div>
        ${data.breakdown ? `
        <div class="label">Breakdown:</div>
        <div class="value">
            Minute: ${data.breakdown.minute} | 
            Hour: ${data.breakdown.hour} | 
            Day: ${data.breakdown.dayOfMonth} | 
            Month: ${data.breakdown.month} | 
            Weekday: ${data.breakdown.dayOfWeek}
        </div>
        ` : ''}
    `;
}

function showError(resultDiv, errorMessage) {
    resultDiv.className = 'result-box error';
    resultDiv.innerHTML = `
        <div class="label">❌ Invalid Expression</div>
        <div class="value">${errorMessage}</div>
    `;
}

function showNextExecutions(executions) {
    const list = document.getElementById('executionList');
    list.innerHTML = '';
    
    executions.forEach((execution, index) => {
        const li = document.createElement('li');
        li.textContent = `${index + 1}. ${execution}`;
        list.appendChild(li);
    });
}

async function generateExpression() {
    const type = document.getElementById('scheduleType').value;
    const minute = document.getElementById('minute').value || '0';
    const hour = document.getElementById('hour').value || '0';
    const dayOfWeek = document.getElementById('dayOfWeek').value || '*';
    const dayOfMonth = document.getElementById('dayOfMonth').value || '1';
    const resultDiv = document.getElementById('generatedResult');

    try {
        const response = await fetch('/api/cron/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                type: type,
                minute: minute,
                hour: hour,
                dayOfWeek: dayOfWeek,
                dayOfMonth: dayOfMonth,
                month: '*'
            })
        });

        const data = await response.json();
        
        resultDiv.className = 'result-box success';
        resultDiv.innerHTML = `
            <div class="label">✨ Generated Expression</div>
            <div class="value">${data.expression}</div>
            <div class="label">Meaning:</div>
            <div class="value">${data.humanReadable}</div>
        `;

        document.getElementById('cronExpression').value = data.expression;

    } catch (error) {
        resultDiv.className = 'result-box error';
        resultDiv.innerHTML = `
            <div class="label">❌ Generation Failed</div>
            <div class="value">${error.message}</div>
        `;
    }
}

function updateGeneratorFields() {
    const type = document.getElementById('scheduleType').value;
    const minuteField = document.getElementById('minuteField');
    const hourField = document.getElementById('hourField');
    const dayOfWeekField = document.getElementById('dayOfWeekField');
    const dayOfMonthField = document.getElementById('dayOfMonthField');

    minuteField.style.display = 'none';
    hourField.style.display = 'none';
    dayOfWeekField.style.display = 'none';
    dayOfMonthField.style.display = 'none';

    switch(type) {
        case 'HOURLY':
            minuteField.style.display = 'block';
            break;
        case 'DAILY':
            minuteField.style.display = 'block';
            hourField.style.display = 'block';
            break;
        case 'WEEKLY':
            minuteField.style.display = 'block';
            hourField.style.display = 'block';
            dayOfWeekField.style.display = 'block';
            break;
        case 'MONTHLY':
            minuteField.style.display = 'block';
            hourField.style.display = 'block';
            dayOfMonthField.style.display = 'block';
            break;
        case 'CUSTOM':
            minuteField.style.display = 'block';
            hourField.style.display = 'block';
            dayOfWeekField.style.display = 'block';
            dayOfMonthField.style.display = 'block';
            break;
    }
}

function useTemplate(expression) {
    document.getElementById('cronExpression').value = expression;
    validateExpression();
}

document.addEventListener('DOMContentLoaded', () => {
    updateGeneratorFields();
    updateMetrics();
    
    document.getElementById('cronExpression').addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            validateExpression();
        }
    });
});
