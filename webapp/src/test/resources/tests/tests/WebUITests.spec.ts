import { test, expect } from '@playwright/test';

test('checkCredentials successful', async ({ page}) => {
    await page.goto('http://localhost:8080/login');

    await page.getByLabel('username').fill(process.env.UNAME);

    await page.getByLabel('password').fill(process.env.PASSWORD);

    await page.getByRole('button', { name: 'Sign in' }).click();

    await page.waitForURL('http://localhost:8080');
});

test('checkCredentials failed', async ({ page}) => {
    await page.goto('http://localhost:8080/login');

    await page.getByLabel('username').fill('user123');

    await page.getByLabel('password').fill('1234');

    await page.getByRole('button', { name: 'Sign in' }).click();

    await page.waitForURL('http://localhost:8080/login?error');
});

test('postNewJob', async ({ page }) => {
    await page.goto('http://localhost:8080/login');

    await page.getByLabel('username').fill(process.env.UNAME);

    await page.getByLabel('password').fill(process.env.PASSWORD);

    await page.getByRole('button', { name: 'Sign in' }).click();

    await page.waitForURL('http://localhost:8080');

    await page.fill('#jobName', 'job1');

    await page.fill('#ownersEmail', '123@web.de');

    await page.fill('#boundingBox', '123.4,12.3,120.5,67.2');

    await page.click('#createNewJob');

    await page.waitForTimeout(2000);

    const jobNameElement = await page.$('td:has-text("job1")');
    const emailElement = await page.$('td:has-text("123@web.de")');

    await expect(jobNameElement).toBeTruthy();
    await expect(emailElement).toBeTruthy();

});






